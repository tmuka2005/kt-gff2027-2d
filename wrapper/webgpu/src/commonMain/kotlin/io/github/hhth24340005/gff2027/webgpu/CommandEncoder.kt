package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * Records GPU commands. Obtained from [Device.submitCommands].
 */
@JvmInline
public expect value class CommandEncoder internal constructor(
  internal val handle: WebGpuNativeHandle,
) {
  /**
   * Records a render pass that draws to [colorAttachments]. The pass ends when
   * [block] returns.
   */
  public inline fun renderPass(
    colorAttachments: List<RenderPassColorAttachment>,
    block: RenderPassEncoder.() -> Unit,
  )
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
