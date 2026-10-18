package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.webgpu_h.wgpuRenderPipelineGetBindGroupLayout

@JvmInline
public actual value class RenderPipeline internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
) {
  context(scope: AutoCloseScope)
  public actual fun getBindGroupLayout(
    index: Int,
  ): BindGroupLayout =
    getBindGroupLayoutImpl(index).also { layout ->
      scope.onClose { layout.release() }
    }
}

// region internal

private fun RenderPipeline.getBindGroupLayoutImpl(
  index: Int,
): BindGroupLayout =
  BindGroupLayout(wgpuRenderPipelineGetBindGroupLayout(handle, index))

// endregion
