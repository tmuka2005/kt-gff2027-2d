package io.github.hhth24340005.gff2027.webgpu

/** The function in a [ShaderModule] that runs the vertex stage. */
public sealed interface VertexEntryPoint {
  /** The only `@vertex` function in the module. */
  public value object Auto : VertexEntryPoint

  /** The `@vertex` function named [name]. */
  public value class Named(public val name: String) : VertexEntryPoint
}

/** Returns the `@vertex` function named [name]. */
public fun VertexEntryPoint(
  name: String,
): VertexEntryPoint =
  VertexEntryPoint.Named(name)
