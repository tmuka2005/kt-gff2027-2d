package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * Shaders and fixed-function state for drawing. Obtained from
 * [Device.createRenderPipeline].
 */
@JvmInline
public expect value class RenderPipeline internal constructor(
  internal val handle: WebGpuNativeHandle,
)
