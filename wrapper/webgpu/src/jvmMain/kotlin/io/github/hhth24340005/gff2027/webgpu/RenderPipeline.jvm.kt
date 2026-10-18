package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.webgpu_h.wgpuRenderPipelineGetBindGroupLayout
import java.lang.foreign.MemorySegment

public actual class RenderPipeline internal constructor(
  internal val handle: MemorySegment,
) {
  context(scope: AutoCloseScope)
  public actual fun getBindGroupLayout(
    index: Int,
  ): BindGroupLayout =
    scope.autoClose(
      acquire = { getBindGroupLayoutImpl(index) },
      release = { layout, _ -> layout.release() },
    )
}

// region internal

private fun RenderPipeline.getBindGroupLayoutImpl(
  index: Int,
): BindGroupLayout =
  BindGroupLayout(wgpuRenderPipelineGetBindGroupLayout(handle, index))

// endregion
