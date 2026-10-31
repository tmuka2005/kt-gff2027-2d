package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * The resource types a [BindGroup] holds. Obtained from
 * [Device.createBindGroupLayout] or [RenderPipeline.getBindGroupLayout].
 */
@JvmInline
public expect value class BindGroupLayout internal constructor(
  internal val handle: WebGpuNativeHandle,
)

/**
 * Declares that `@binding(binding)` holds a resource of [type], which both
 * the vertex and fragment stages can read.
 */
public value class BindGroupLayoutEntry(
  public val binding: Int,
  public val type: BindingType,
)

/** The type of resource a [BindGroupLayoutEntry] declares. */
public enum class BindingType {
  /** A 2D texture of floats, as `texture_2d<f32>`. */
  Texture,

  /** A sampler that may blend texels, as `sampler`. */
  Sampler,
}
