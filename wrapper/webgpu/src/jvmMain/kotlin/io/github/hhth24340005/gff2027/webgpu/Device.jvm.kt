package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.WGPUBindGroupDescriptor
import wgpu_native.WGPUBindGroupEntry
import wgpu_native.WGPUBufferDescriptor
import wgpu_native.WGPUChainedStruct
import wgpu_native.WGPUColorTargetState
import wgpu_native.WGPUExtent3D
import wgpu_native.WGPUFragmentState
import wgpu_native.WGPUMultisampleState
import wgpu_native.WGPUPrimitiveState
import wgpu_native.WGPURenderPipelineDescriptor
import wgpu_native.WGPUSamplerDescriptor
import wgpu_native.WGPUShaderModuleDescriptor
import wgpu_native.WGPUShaderSourceWGSL
import wgpu_native.WGPUTextureDescriptor
import wgpu_native.WGPUVertexState
import wgpu_native.webgpu_h.WGPUAddressMode_ClampToEdge
import wgpu_native.webgpu_h.WGPUMipmapFilterMode_Nearest
import wgpu_native.webgpu_h.WGPUPrimitiveTopology_TriangleList
import wgpu_native.webgpu_h.WGPUSType_ShaderSourceWGSL
import wgpu_native.webgpu_h.WGPUTextureDimension_2D
import wgpu_native.webgpu_h.WGPU_WHOLE_SIZE
import wgpu_native.webgpu_h.wgpuBindGroupRelease
import wgpu_native.webgpu_h.wgpuBufferRelease
import wgpu_native.webgpu_h.wgpuCommandEncoderRelease
import wgpu_native.webgpu_h.wgpuDeviceCreateBindGroup
import wgpu_native.webgpu_h.wgpuDeviceCreateBuffer
import wgpu_native.webgpu_h.wgpuDeviceCreateCommandEncoder
import wgpu_native.webgpu_h.wgpuDeviceCreateRenderPipeline
import wgpu_native.webgpu_h.wgpuDeviceCreateSampler
import wgpu_native.webgpu_h.wgpuDeviceCreateShaderModule
import wgpu_native.webgpu_h.wgpuDeviceCreateTexture
import wgpu_native.webgpu_h.wgpuDeviceGetQueue
import wgpu_native.webgpu_h.wgpuRenderPipelineRelease
import wgpu_native.webgpu_h.wgpuSamplerRelease
import wgpu_native.webgpu_h.wgpuShaderModuleRelease
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySegment.NULL

public actual class Device internal constructor(
  internal val handle: MemorySegment,
) {
  public actual val queue: Queue = Queue(wgpuDeviceGetQueue(handle))

  context(scope: AutoCloseScope)
  public actual fun createShaderModule(
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    wgsl: String,
    label: String?,
  ): ShaderModule =
    scope.autoClose(
      acquire = { createShaderModuleImpl(wgsl, label) },
      release = { module, _ -> module.release() },
    )

  context(scope: AutoCloseScope)
  public actual fun createRenderPipeline(
    vertex: VertexState,
    fragment: FragmentState,
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    label: String?,
  ): RenderPipeline =
    scope.autoClose(
      acquire = { createRenderPipelineImpl(vertex, fragment, label) },
      release = { pipeline, _ -> pipeline.release() },
    )

  context(scope: AutoCloseScope)
  public actual fun createBuffer(
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    size: Int,
    usage: Set<BufferUsage>,
    label: String?,
  ): Buffer =
    scope.autoClose(
      acquire = { createBufferImpl(size, usage, label) },
      release = { buffer, _ -> buffer.release() },
    )

  context(scope: AutoCloseScope)
  public actual fun createTexture(
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    size: Pair<Int, Int>,
    format: TextureFormat,
    label: String?,
  ): Texture =
    scope.autoClose(
      acquire = { createTextureImpl(size, format, label) },
      release = { texture, _ -> texture.release() },
    )

  context(scope: AutoCloseScope)
  public actual fun createSampler(
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    magFilter: FilterMode,
    minFilter: FilterMode,
    label: String?,
  ): Sampler =
    scope.autoClose(
      acquire = { createSamplerImpl(magFilter, minFilter, label) },
      release = { sampler, _ -> sampler.release() },
    )

  context(scope: AutoCloseScope)
  public actual fun createBindGroup(
    layout: BindGroupLayout,
    entries: List<BindGroupEntry>,
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    label: String?,
  ): BindGroup =
    scope.autoClose(
      acquire = { createBindGroupImpl(layout, entries, label) },
      release = { group, _ -> group.release() },
    )

  context(scope: AutoCloseScope)
  public actual fun createCommandEncoder(): CommandEncoder =
    scope.autoClose(
      acquire = { createCommandEncoderImpl() },
      release = { encoder, _ -> encoder.release() },
    )
}

// region internal

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

private fun Device.createBufferImpl(
  size: Int,
  usage: Set<BufferUsage>,
  label: String?,
): Buffer =
  Arena.ofConfined().use { arena ->
    Buffer(
      wgpuDeviceCreateBuffer(
        handle,
        bufferDescriptor(arena, size, usage, label),
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

private fun Device.createSamplerImpl(
  magFilter: FilterMode,
  minFilter: FilterMode,
  label: String?,
): Sampler =
  Arena.ofConfined().use { arena ->
    Sampler(
      wgpuDeviceCreateSampler(
        handle,
        samplerDescriptor(arena, magFilter, minFilter, label),
      ),
    )
  }

private fun Device.createBindGroupImpl(
  layout: BindGroupLayout,
  entries: List<BindGroupEntry>,
  label: String?,
): BindGroup =
  Arena.ofConfined().use { arena ->
    BindGroup(
      wgpuDeviceCreateBindGroup(
        handle,
        bindGroupDescriptor(arena, layout, entries, label),
      ),
    )
  }

private fun Device.createCommandEncoderImpl(): CommandEncoder =
  CommandEncoder(wgpuDeviceCreateCommandEncoder(handle, NULL))

private fun ShaderModule.release() {
  wgpuShaderModuleRelease(handle)
}

private fun RenderPipeline.release() {
  wgpuRenderPipelineRelease(handle)
}

private fun Buffer.release() {
  wgpuBufferRelease(handle)
}

private fun Sampler.release() {
  wgpuSamplerRelease(handle)
}

private fun BindGroup.release() {
  wgpuBindGroupRelease(handle)
}

private fun CommandEncoder.release() {
  wgpuCommandEncoderRelease(handle)
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
  val entryPoint = WGPUVertexState.entryPoint(this)
  when (val point = vertex.entryPoint) {
    VertexEntryPoint.Auto -> entryPoint.setNullStringView()
    is VertexEntryPoint.Named -> entryPoint.setStringView(arena, point.name)
  }
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
  val entryPoint = WGPUFragmentState.entryPoint(state)
  when (val point = fragment.entryPoint) {
    FragmentEntryPoint.Auto -> entryPoint.setNullStringView()
    is FragmentEntryPoint.Named -> entryPoint.setStringView(arena, point.name)
  }
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

private fun bufferDescriptor(
  arena: Arena,
  size: Int,
  usage: Set<BufferUsage>,
  label: String?,
): MemorySegment {
  val descriptor = WGPUBufferDescriptor.allocate(arena)
  if (label != null) {
    WGPUBufferDescriptor.label(descriptor).setStringView(arena, label)
  }
  WGPUBufferDescriptor.usage(
    descriptor,
    usage.fold(0L) { flags, each -> flags or each.native },
  )
  WGPUBufferDescriptor.size(descriptor, size.toLong())
  return descriptor
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

private fun samplerDescriptor(
  arena: Arena,
  magFilter: FilterMode,
  minFilter: FilterMode,
  label: String?,
): MemorySegment {
  val descriptor = WGPUSamplerDescriptor.allocate(arena)
  if (label != null) {
    WGPUSamplerDescriptor.label(descriptor).setStringView(arena, label)
  }
  WGPUSamplerDescriptor.addressModeU(descriptor, WGPUAddressMode_ClampToEdge())
  WGPUSamplerDescriptor.addressModeV(descriptor, WGPUAddressMode_ClampToEdge())
  WGPUSamplerDescriptor.addressModeW(descriptor, WGPUAddressMode_ClampToEdge())
  WGPUSamplerDescriptor.magFilter(descriptor, magFilter.native)
  WGPUSamplerDescriptor.minFilter(descriptor, minFilter.native)
  WGPUSamplerDescriptor
    .mipmapFilter(descriptor, WGPUMipmapFilterMode_Nearest())
  // The defaults of WGPU_SAMPLER_DESCRIPTOR_INIT, as allocate zeroes them
  WGPUSamplerDescriptor.lodMaxClamp(descriptor, LOD_MAX_CLAMP)
  WGPUSamplerDescriptor.maxAnisotropy(descriptor, 1)
  return descriptor
}

private fun bindGroupDescriptor(
  arena: Arena,
  layout: BindGroupLayout,
  entries: List<BindGroupEntry>,
  label: String?,
): MemorySegment {
  val descriptor = WGPUBindGroupDescriptor.allocate(arena)
  if (label != null) {
    WGPUBindGroupDescriptor.label(descriptor).setStringView(arena, label)
  }
  WGPUBindGroupDescriptor.layout(descriptor, layout.handle)
  WGPUBindGroupDescriptor.entryCount(descriptor, entries.size.toLong())
  WGPUBindGroupDescriptor.entries(descriptor, bindGroupEntries(arena, entries))
  return descriptor
}

private fun bindGroupEntries(
  arena: Arena,
  entries: List<BindGroupEntry>,
): MemorySegment {
  val structs =
    WGPUBindGroupEntry.allocateArray(entries.size.toLong(), arena)
  entries.forEachIndexed { index, entry ->
    val struct = WGPUBindGroupEntry.asSlice(structs, index.toLong())
    WGPUBindGroupEntry.binding(struct, entry.binding)
    when (val resource = entry.resource) {
      is Buffer -> struct.setWholeBuffer(resource)
      is Sampler -> WGPUBindGroupEntry.sampler(struct, resource.handle)
      is TextureView -> WGPUBindGroupEntry.textureView(struct, resource.handle)
    }
  }
  return structs
}

private fun MemorySegment.setWholeBuffer(
  buffer: Buffer,
) {
  WGPUBindGroupEntry.buffer(this, buffer.handle)
  WGPUBindGroupEntry.size(this, WGPU_WHOLE_SIZE())
}

// jextract skips it, as webgpu.h declares it as static const
private const val COLOR_WRITE_MASK_ALL = 0xFL

private val BufferUsage.native: Long
  get() =
    when (this) {
      BufferUsage.CopyDst -> BUFFER_USAGE_COPY_DST
      BufferUsage.Uniform -> BUFFER_USAGE_UNIFORM
    }

// jextract skips them, as webgpu.h declares them as static const
private const val BUFFER_USAGE_COPY_DST = 0x8L
private const val BUFFER_USAGE_UNIFORM = 0x40L

// jextract skips them, as webgpu.h declares them as static const
private const val TEXTURE_USAGE_COPY_DST = 0x2L
private const val TEXTURE_USAGE_TEXTURE_BINDING = 0x4L

private const val LOD_MAX_CLAMP = 32f

// endregion
