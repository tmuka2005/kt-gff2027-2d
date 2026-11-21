package io.github.hhth24340005.gff2027.webgpu

import wgpu_native.webgpu_h.wgpuCreateInstance
import wgpu_native.webgpu_h.wgpuInstanceRelease
import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySegment.NULL

public actual class Instance internal constructor(
  internal val handle: MemorySegment,
)

// region internal

internal actual fun createInstance(): Instance =
  Instance(wgpuCreateInstance(NULL))

internal actual fun Instance.release() {
  wgpuInstanceRelease(handle)
}

// endregion
