package io.github.hhth24340005.gff2027.webgpu

/** Records draw commands in a [CommandEncoder.renderPass]. */
public expect class RenderPassEncoder {
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
}

// region internal

@PublishedApi
internal expect fun RenderPassEncoder.endAndRelease()

// endregion
