package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * A view of a [Texture] that a render pass draws to. Obtained from
 * [Texture.createView].
 */
@JvmInline
public expect value class TextureView internal constructor(
  internal val handle: WebGpuNativeHandle,
)
