package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import kotlin.jvm.JvmInline

/**
 * Records draw commands into a [RenderBundle]. Obtained from
 * [Device.createRenderBundleEncoder].
 */
@JvmInline
public expect value class RenderBundleEncoder internal constructor(
  internal val handle: WebGpuNativeHandle,
) {
  /**
   * Uses [pipeline] for the following draws. See the WebGPU spec:
   * https://www.w3.org/TR/webgpu/#dom-gpurendercommandsmixin-setpipeline
   */
  public fun setPipeline(
    pipeline: RenderPipeline,
  )

  /**
   * Uses [group] as bind group [index] for the following draws. See the WebGPU
   * spec:
   * https://www.w3.org/TR/webgpu/#dom-gpubindingcommandsmixin-setbindgroup
   */
  public fun setBindGroup(
    index: Int,
    group: BindGroup,
  )

  /**
   * Draws primitives with the current pipeline. See the WebGPU spec:
   * https://www.w3.org/TR/webgpu/#dom-gpurendercommandsmixin-draw
   */
  public fun draw(
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    vertexCount: Int,
    instanceCount: Int = 1,
    firstVertex: Int = 0,
    firstInstance: Int = 0,
  )

  /**
   * Draws primitives with the current pipeline, reading `vertexCount`,
   * `instanceCount`, `firstVertex` and `firstInstance` as four u32 from
   * [buffer] at [offset] bytes when the GPU runs the draw. See the WebGPU spec:
   * https://www.w3.org/TR/webgpu/#dom-gpurendercommandsmixin-drawindirect
   */
  public fun drawIndirect(
    buffer: Buffer,
    offset: Int = 0,
  )

  /**
   * Finishes recording. The encoder cannot record after this. The bundle keeps
   * the pipelines and bind groups it uses alive, so the scopes that created
   * them may exit before it.
   */
  context(scope: AutoCloseScope)
  public fun finish(): RenderBundle
}
