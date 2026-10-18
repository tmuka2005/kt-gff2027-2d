package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * How shaders read a texture, such as the filter between texels. Obtained
 * from [Device.createSampler].
 */
@JvmInline
public expect value class Sampler internal constructor(
  internal val handle: WebGpuNativeHandle,
) : BindingResource

/** How a [Sampler] picks colors between texels. */
public enum class FilterMode {
  /** Takes the closest texel, which keeps pixel art sharp. */
  Nearest,

  /** Blends the surrounding texels. */
  Linear,
}
