package io.github.hhth24340005.gff2027.webgpu

import wgpu_native.webgpu_h.wgpuBindGroupLayoutRelease

@JvmInline
public actual value class BindGroupLayout internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
)

// region internal

internal fun BindGroupLayout.release() {
  wgpuBindGroupLayoutRelease(handle)
}

// endregion
