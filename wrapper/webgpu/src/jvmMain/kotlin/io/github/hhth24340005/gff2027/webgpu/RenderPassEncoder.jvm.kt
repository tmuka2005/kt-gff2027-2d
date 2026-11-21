package io.github.hhth24340005.gff2027.webgpu

import wgpu_native.webgpu_h.wgpuRenderPassEncoderEnd
import wgpu_native.webgpu_h.wgpuRenderPassEncoderRelease
import java.lang.foreign.MemorySegment

public actual class RenderPassEncoder internal constructor(
  internal val handle: MemorySegment,
)

// region internal

@PublishedApi
internal actual fun RenderPassEncoder.endAndRelease() {
  wgpuRenderPassEncoderEnd(handle)
  wgpuRenderPassEncoderRelease(handle)
}

// endregion
