package io.github.hhth24340005.gff2027.webgpu

import wgpu_native.webgpu_h.wgpuRenderPassEncoderEnd
import wgpu_native.webgpu_h.wgpuRenderPassEncoderRelease

@JvmInline
public actual value class RenderPassEncoder internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
)

// region internal

@PublishedApi
internal actual fun RenderPassEncoder.endAndRelease() {
  wgpuRenderPassEncoderEnd(handle)
  wgpuRenderPassEncoderRelease(handle)
}

// endregion
