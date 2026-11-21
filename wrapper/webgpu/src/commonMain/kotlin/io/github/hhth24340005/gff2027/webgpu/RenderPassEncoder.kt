package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/** Records draw commands in a [CommandEncoder.renderPass]. */
@JvmInline
public expect value class RenderPassEncoder internal constructor(
  internal val handle: WebGpuNativeHandle,
)

// region internal

@PublishedApi
internal expect fun RenderPassEncoder.endAndRelease()

// endregion
