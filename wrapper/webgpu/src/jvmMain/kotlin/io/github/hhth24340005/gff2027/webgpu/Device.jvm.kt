package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.WGPUChainedStruct
import wgpu_native.WGPUColorTargetState
import wgpu_native.WGPUExtent3D
import wgpu_native.WGPUFragmentState
import wgpu_native.WGPUMultisampleState
import wgpu_native.WGPUPrimitiveState
import wgpu_native.WGPURenderPipelineDescriptor
import wgpu_native.WGPUShaderModuleDescriptor
import wgpu_native.WGPUShaderSourceWGSL
import wgpu_native.WGPUTextureDescriptor
import wgpu_native.WGPUVertexState
import wgpu_native.webgpu_h.WGPUPrimitiveTopology_TriangleList
import wgpu_native.webgpu_h.WGPUSType_ShaderSourceWGSL
import wgpu_native.webgpu_h.WGPUTextureDimension_2D
import wgpu_native.webgpu_h.wgpuCommandBufferRelease
import wgpu_native.webgpu_h.wgpuCommandEncoderFinish
import wgpu_native.webgpu_h.wgpuCommandEncoderRelease
import wgpu_native.webgpu_h.wgpuDeviceCreateCommandEncoder
import wgpu_native.webgpu_h.wgpuDeviceCreateRenderPipeline
import wgpu_native.webgpu_h.wgpuDeviceCreateShaderModule
import wgpu_native.webgpu_h.wgpuDeviceCreateTexture
import wgpu_native.webgpu_h.wgpuQueueSubmit
import wgpu_native.webgpu_h.wgpuRenderPipelineRelease
import wgpu_native.webgpu_h.wgpuShaderModuleRelease
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySegment.NULL
import java.lang.foreign.ValueLayout.ADDRESS

public actual value class Device internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
  public actual val queue: Queue,
) {
  context(scope: AutoCloseScope)
  public actual fun createShaderModule(
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    wgsl: String,
    label: String?,
  ): ShaderModule =
    createShaderModuleImpl(wgsl, label).also { module ->
      scope.onClose { module.release() }
    }

  context(scope: AutoCloseScope)
  public actual fun createRenderPipeline(
    vertex: VertexState,
    fragment: FragmentState,
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    label: String?,
  ): RenderPipeline =
    createRenderPipelineImpl(vertex, fragment, label).also { pipeline ->
      scope.onClose { pipeline.release() }
    }

  context(scope: AutoCloseScope)
  public actual fun createTexture(
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    size: Pair<Int, Int>,
    format: TextureFormat,
    label: String?,
  ): Texture =
    createTextureImpl(size, format, label).also { texture ->
      scope.onClose { texture.release() }
    }

  public actual inline fun submitCommands(
    block: CommandEncoder.() -> Unit,
  ) {
    submitCommandsImpl(block)
  }
}

// region internal

@PublishedApi
internal actual fun Device.beginCommands(): CommandEncoder =
  CommandEncoder(wgpuDeviceCreateCommandEncoder(handle, NULL))

@PublishedApi
internal actual fun Device.finishAndSubmit(
  encoder: CommandEncoder,
) {
  val commands = wgpuCommandEncoderFinish(encoder.handle, NULL)
  try {
    Arena.ofConfined().use { arena ->
      wgpuQueueSubmit(
        queue.handle,
        1L,
        arena.allocateFrom(ADDRESS, commands),
      )
    }
  } finally {
    wgpuCommandBufferRelease(commands)
  }
}

@PublishedApi
internal actual fun CommandEncoder.release() {
  wgpuCommandEncoderRelease(handle)
}

private fun Device.createShaderModuleImpl(
  wgsl: String,
  label: String?,
): ShaderModule =
  Arena.ofConfined().use { arena ->
    ShaderModule(
      wgpuDeviceCreateShaderModule(
        handle,
        shaderModuleDescriptor(arena, wgsl, label),
      ),
    )
  }

private fun Device.createRenderPipelineImpl(
  vertex: VertexState,
  fragment: FragmentState,
  label: String?,
): RenderPipeline =
  Arena.ofConfined().use { arena ->
    RenderPipeline(
      wgpuDeviceCreateRenderPipeline(
        handle,
        renderPipelineDescriptor(arena, vertex, fragment, label),
      ),
    )
  }

private fun Device.createTextureImpl(
  size: Pair<Int, Int>,
  format: TextureFormat,
  label: String?,
): Texture =
  Arena.ofConfined().use { arena ->
    Texture(
      wgpuDeviceCreateTexture(
        handle,
        textureDescriptor(arena, size, format, label),
      ),
    )
  }

private fun ShaderModule.release() {
  wgpuShaderModuleRelease(handle)
}

private fun RenderPipeline.release() {
  wgpuRenderPipelineRelease(handle)
}

private fun shaderModuleDescriptor(
  arena: Arena,
  wgsl: String,
  label: String?,
): MemorySegment {
  val descriptor = WGPUShaderModuleDescriptor.allocate(arena)
  WGPUShaderModuleDescriptor.nextInChain(descriptor, wgslSource(arena, wgsl))
  if (label != null) {
    WGPUShaderModuleDescriptor.label(descriptor).setStringView(arena, label)
  }
  return descriptor
}

private fun wgslSource(
  arena: Arena,
  wgsl: String,
): MemorySegment {
  val source = WGPUShaderSourceWGSL.allocate(arena)
  WGPUChainedStruct.sType(
    WGPUShaderSourceWGSL.chain(source),
    WGPUSType_ShaderSourceWGSL(),
  )
  WGPUShaderSourceWGSL.code(source).setStringView(arena, wgsl)
  return source
}

private fun renderPipelineDescriptor(
  arena: Arena,
  vertex: VertexState,
  fragment: FragmentState,
  label: String?,
): MemorySegment {
  val descriptor = WGPURenderPipelineDescriptor.allocate(arena)
  if (label != null) {
    WGPURenderPipelineDescriptor.label(descriptor).setStringView(arena, label)
  }
  WGPURenderPipelineDescriptor.vertex(descriptor).setVertexState(arena, vertex)
  WGPUPrimitiveState.topology(
    WGPURenderPipelineDescriptor.primitive(descriptor),
    WGPUPrimitiveTopology_TriangleList(),
  )
  WGPURenderPipelineDescriptor
    .multisample(descriptor)
    .setSingleSample()
  WGPURenderPipelineDescriptor
    .fragment(descriptor, fragmentState(arena, fragment))
  return descriptor
}

private fun MemorySegment.setVertexState(
  arena: Arena,
  vertex: VertexState,
) {
  WGPUVertexState.module(this, vertex.module.handle)
  WGPUVertexState.entryPoint(this).setStringView(arena, vertex.entryPoint)
}

private fun MemorySegment.setSingleSample() {
  WGPUMultisampleState.count(this, 1)
  // Every bit set enables all samples
  WGPUMultisampleState.mask(this, -1)
}

private fun fragmentState(
  arena: Arena,
  fragment: FragmentState,
): MemorySegment {
  val state = WGPUFragmentState.allocate(arena)
  WGPUFragmentState.module(state, fragment.module.handle)
  WGPUFragmentState.entryPoint(state).setStringView(arena, fragment.entryPoint)
  WGPUFragmentState.targetCount(
    state,
    fragment.targets.size
      .toLong(),
  )
  WGPUFragmentState.targets(state, colorTargetStates(arena, fragment.targets))
  return state
}

private fun colorTargetStates(
  arena: Arena,
  targets: List<ColorTargetState>,
): MemorySegment {
  val states =
    WGPUColorTargetState.allocateArray(targets.size.toLong(), arena)
  targets.forEachIndexed { index, target ->
    val state = WGPUColorTargetState.asSlice(states, index.toLong())
    WGPUColorTargetState.format(state, target.format.native)
    WGPUColorTargetState.writeMask(state, COLOR_WRITE_MASK_ALL)
  }
  return states
}

private fun textureDescriptor(
  arena: Arena,
  size: Pair<Int, Int>,
  format: TextureFormat,
  label: String?,
): MemorySegment {
  val [width, height] = size
  val descriptor = WGPUTextureDescriptor.allocate(arena)
  if (label != null) {
    WGPUTextureDescriptor.label(descriptor).setStringView(arena, label)
  }
  WGPUTextureDescriptor.usage(
    descriptor,
    TEXTURE_USAGE_COPY_DST or TEXTURE_USAGE_TEXTURE_BINDING,
  )
  WGPUTextureDescriptor.dimension(descriptor, WGPUTextureDimension_2D())
  val extent = WGPUTextureDescriptor.size(descriptor)
  WGPUExtent3D.width(extent, width)
  WGPUExtent3D.height(extent, height)
  WGPUExtent3D.depthOrArrayLayers(extent, 1)
  WGPUTextureDescriptor.format(descriptor, format.native)
  WGPUTextureDescriptor.mipLevelCount(descriptor, 1)
  WGPUTextureDescriptor.sampleCount(descriptor, 1)
  return descriptor
}

// jextract skips it, as webgpu.h declares it as static const
private const val COLOR_WRITE_MASK_ALL = 0xFL

// jextract skips them, as webgpu.h declares them as static const
private const val TEXTURE_USAGE_COPY_DST = 0x2L
private const val TEXTURE_USAGE_TEXTURE_BINDING = 0x4L

// endregion
