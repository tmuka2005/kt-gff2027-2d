package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * The bind group layouts a [RenderPipeline] uses, in group order. Obtained
 * from [Device.createPipelineLayout].
 */
@JvmInline
public expect value class PipelineLayout internal constructor(
  internal val handle: WebGpuNativeHandle,
) {
  public companion object {
    public val Auto: PipelineLayout
  }
}
