package io.github.hhth24340005.gff2027.webgpu

/**
 * The fragment stage of a [RenderPipeline]. Each of [targets] matches the
 * color attachment at the same index in a render pass.
 */
public value class FragmentState private constructor(
  public val module: ShaderModule,
  public val targets: List<ColorTargetState>,
  public val entryPoint: FragmentEntryPoint,
) {
  public constructor(
    module: ShaderModule,
    targets: List<ColorTargetState>,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    entryPoint: FragmentEntryPoint = Auto,
  ) : this(module, targets, entryPoint)
}
