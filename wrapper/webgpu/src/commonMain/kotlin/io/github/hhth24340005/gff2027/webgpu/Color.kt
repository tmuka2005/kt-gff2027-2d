package io.github.hhth24340005.gff2027.webgpu

/** A linear RGBA color with components from 0 to 1. */
public value class Color private constructor(
  public val red: Double,
  public val green: Double,
  public val blue: Double,
  public val alpha: Double,
) {
  public constructor(
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    red: Double,
    green: Double,
    blue: Double,
    alpha: Double = 1.0,
  ) : this(red, green, blue, alpha)
}
