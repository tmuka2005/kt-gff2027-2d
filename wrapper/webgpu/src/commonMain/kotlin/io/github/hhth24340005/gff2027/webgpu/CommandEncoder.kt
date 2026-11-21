package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope

/**
 * Records GPU commands into a [CommandBuffer]. Obtained from
 * [Device.createCommandEncoder].
 */
public expect class CommandEncoder {
  /**
   * Records a render pass that draws to [colorAttachments]. The pass ends when
   * [block] returns.
   */
  public inline fun renderPass(
    colorAttachments: List<RenderPassColorAttachment>,
    block: RenderPassEncoder.() -> Unit,
  )

  /** Finishes recording. The encoder cannot record after this. */
  context(scope: AutoCloseScope)
  public fun finish(): CommandBuffer
}

// region internal

@PublishedApi
internal inline fun CommandEncoder.renderPassImpl(
  colorAttachments: List<RenderPassColorAttachment>,
  block: RenderPassEncoder.() -> Unit,
) {
  val pass = beginRenderPass(colorAttachments)
  try {
    pass.block()
  } finally {
    pass.endAndRelease()
  }
}

@PublishedApi
internal expect fun CommandEncoder.beginRenderPass(
  colorAttachments: List<RenderPassColorAttachment>,
): RenderPassEncoder

// endregion
