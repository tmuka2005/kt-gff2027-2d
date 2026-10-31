package io.github.hhth24340005.gff2027.webgpu

import wgpu_native.webgpu_h.wgpuRenderPassEncoderDraw
import wgpu_native.webgpu_h.wgpuRenderPassEncoderEnd
import wgpu_native.webgpu_h.wgpuRenderPassEncoderExecuteBundles
import wgpu_native.webgpu_h.wgpuRenderPassEncoderRelease
import wgpu_native.webgpu_h.wgpuRenderPassEncoderSetBindGroup
import wgpu_native.webgpu_h.wgpuRenderPassEncoderSetPipeline
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment.NULL
import java.lang.foreign.ValueLayout.ADDRESS

@JvmInline
public actual value class RenderPassEncoder internal actual constructor(
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

  public actual fun executeBundles(
    bundles: List<RenderBundle>,
  ) {
    executeBundlesImpl(bundles)
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

private fun RenderPassEncoder.setBindGroupImpl(
  index: Int,
  group: BindGroup,
) {
  wgpuRenderPassEncoderSetBindGroup(handle, index, group.handle, 0L, NULL)
}

private fun RenderPassEncoder.executeBundlesImpl(
  bundles: List<RenderBundle>,
) {
  Arena.ofConfined().use { arena ->
    val handles = arena.allocate(ADDRESS, bundles.size.toLong())
    bundles.forEachIndexed { index, bundle ->
      handles.setAtIndex(ADDRESS, index.toLong(), bundle.handle)
    }
    wgpuRenderPassEncoderExecuteBundles(handle, bundles.size.toLong(), handles)
  }
}

// endregion
