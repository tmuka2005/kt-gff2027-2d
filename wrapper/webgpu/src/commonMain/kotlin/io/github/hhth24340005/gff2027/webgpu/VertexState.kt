package io.github.hhth24340005.gff2027.webgpu

/** The vertex stage of a [RenderPipeline]. */
public value class VertexState private constructor(
  public val module: ShaderModule,
  public val entryPoint: VertexEntryPoint,
) {
  public constructor(
    module: ShaderModule,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    entryPoint: VertexEntryPoint = Auto,
  ) : this(module, entryPoint)
}
