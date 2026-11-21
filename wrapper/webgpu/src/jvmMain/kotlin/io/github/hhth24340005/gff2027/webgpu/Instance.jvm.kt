package io.github.hhth24340005.gff2027.webgpu

import wgpu_native.webgpu_h.wgpuCreateInstance
import wgpu_native.webgpu_h.wgpuInstanceRelease
import java.lang.foreign.MemorySegment.NULL

@JvmInline
public actual value class Instance internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
)

// region internal

internal actual fun createInstance(): Instance =
  Instance(wgpuCreateInstance(NULL))

internal actual fun Instance.release() {
  wgpuInstanceRelease(handle)
}

// endregion
