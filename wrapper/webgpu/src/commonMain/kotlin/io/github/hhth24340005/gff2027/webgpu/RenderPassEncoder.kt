package io.github.hhth24340005.gff2027.webgpu

/** Records draw commands in a [CommandEncoder.renderPass]. */
public expect class RenderPassEncoder

// region internal

@PublishedApi
internal expect fun RenderPassEncoder.endAndRelease()

// endregion
