package io.github.hhth24340005.gff2027.webgpu

import wgpu_native.webgpu_h.wgpuTextureRelease

@JvmInline
public actual value class Texture internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
)

// region internal

internal fun Texture.release() {
  wgpuTextureRelease(handle)
}

// endregion
