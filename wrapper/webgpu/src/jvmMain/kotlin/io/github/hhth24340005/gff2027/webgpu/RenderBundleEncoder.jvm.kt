package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.webgpu_h.wgpuRenderBundleEncoderDraw
import wgpu_native.webgpu_h.wgpuRenderBundleEncoderFinish
import wgpu_native.webgpu_h.wgpuRenderBundleEncoderSetBindGroup
import wgpu_native.webgpu_h.wgpuRenderBundleEncoderSetPipeline
import wgpu_native.webgpu_h.wgpuRenderBundleRelease
import java.lang.foreign.MemorySegment.NULL

@JvmInline
public actual value class RenderBundleEncoder internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
) {
  public actual fun setPipeline(
    pipeline: RenderPipeline,
  ) {
    setPipelineImpl(pipeline)
  }

  public actual fun setBindGroup(
    index: Int,
    group: BindGroup,
  ) {
    setBindGroupImpl(index, group)
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

  context(scope: AutoCloseScope)
  public actual fun finish(): RenderBundle =
    finishImpl().also { bundle ->
      scope.onClose { bundle.release() }
    }
}

// region internal

private fun RenderBundleEncoder.setPipelineImpl(
  pipeline: RenderPipeline,
) {
  wgpuRenderBundleEncoderSetPipeline(handle, pipeline.handle)
}

private fun RenderBundleEncoder.setBindGroupImpl(
  index: Int,
  group: BindGroup,
) {
  wgpuRenderBundleEncoderSetBindGroup(handle, index, group.handle, 0L, NULL)
}

private fun RenderBundleEncoder.drawImpl(
  vertexCount: Int,
  instanceCount: Int,
  firstVertex: Int,
  firstInstance: Int,
) {
  wgpuRenderBundleEncoderDraw(
    handle,
    vertexCount,
    instanceCount,
    firstVertex,
    firstInstance,
  )
}

private fun RenderBundleEncoder.finishImpl(): RenderBundle =
  RenderBundle(wgpuRenderBundleEncoderFinish(handle, NULL))

private fun RenderBundle.release() {
  wgpuRenderBundleRelease(handle)
}

// endregion
