package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.WGPUChainedStruct
import wgpu_native.WGPUShaderModuleDescriptor
import wgpu_native.WGPUShaderSourceWGSL
import wgpu_native.webgpu_h.WGPUSType_ShaderSourceWGSL
import wgpu_native.webgpu_h.wgpuDeviceCreateShaderModule
import wgpu_native.webgpu_h.wgpuShaderModuleRelease
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

public actual class Device internal constructor(
  internal val handle: MemorySegment,
) {
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

private fun ShaderModule.release() {
  wgpuShaderModuleRelease(handle)
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

// endregion
