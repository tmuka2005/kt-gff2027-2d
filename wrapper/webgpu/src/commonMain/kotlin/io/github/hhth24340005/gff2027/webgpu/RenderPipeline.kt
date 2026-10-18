package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope

/**
 * Shaders and fixed-function state for drawing. Obtained from
 * [Device.createRenderPipeline].
 */
public expect class RenderPipeline {
  /**
   * Returns the layout of bind group [index], derived from the pipeline's
   * shaders. The scope releases it when it exits.
   */
  context(scope: AutoCloseScope)
  public fun getBindGroupLayout(
    index: Int,
  ): BindGroupLayout
}
