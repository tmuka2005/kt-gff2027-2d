package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import kotlin.jvm.JvmInline

/**
 * Shaders and fixed-function state for drawing. Obtained from
 * [Device.createRenderPipeline].
 */
@JvmInline
public expect value class RenderPipeline internal constructor(
  internal val handle: WebGpuNativeHandle,
) {
  /**
   * Returns the layout of bind group [index], derived from the pipeline's
   * shaders. The scope releases it when it exits.
   */
  context(scope: AutoCloseScope)
  public fun getBindGroupLayout(
    index: Int,
  ): BindGroupLayout
}
