package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/** An image on the GPU. Obtained from [Surface.getCurrentTexture]. */
@JvmInline
public expect value class Texture internal constructor(
  internal val handle: WebGpuNativeHandle,
)
