package io.github.hhth24340005.gff2027.webgpu

import wgpu_native.webgpu_h.wgpuRenderPassEncoderDraw
import wgpu_native.webgpu_h.wgpuRenderPassEncoderEnd
import wgpu_native.webgpu_h.wgpuRenderPassEncoderRelease
import wgpu_native.webgpu_h.wgpuRenderPassEncoderSetPipeline

@JvmInline
public actual value class RenderPassEncoder internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
) {
  public actual fun setPipeline(
    pipeline: RenderPipeline,
  ) {
    setPipelineImpl(pipeline)
  }

  public actual fun draw(
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    vertexCount: Int,
    instanceCount: Int,
    firstVertex: Int,
    firstInstance: Int,
  ) {
    drawImpl(vertexCount, instanceCount, firstVertex, firstInstance)
  }
}

// region internal

@PublishedApi
internal actual fun RenderPassEncoder.endAndRelease() {
  wgpuRenderPassEncoderEnd(handle)
  wgpuRenderPassEncoderRelease(handle)
}

private fun RenderPassEncoder.setPipelineImpl(
  pipeline: RenderPipeline,
) {
  wgpuRenderPassEncoderSetPipeline(handle, pipeline.handle)
}

private fun RenderPassEncoder.drawImpl(
  vertexCount: Int,
  instanceCount: Int,
  firstVertex: Int,
  firstInstance: Int,
) {
  wgpuRenderPassEncoderDraw(
    handle,
    vertexCount,
    instanceCount,
    firstVertex,
    firstInstance,
  )
}

// endregion
