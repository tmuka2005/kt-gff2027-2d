package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * The resource types a [BindGroup] holds. Obtained from
 * [RenderPipeline.getBindGroupLayout].
 */
@JvmInline
public expect value class BindGroupLayout internal constructor(
  internal val handle: WebGpuNativeHandle,
)
