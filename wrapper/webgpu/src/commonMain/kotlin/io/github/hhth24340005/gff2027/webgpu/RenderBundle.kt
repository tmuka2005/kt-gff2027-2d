package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * Recorded draw commands that render passes can replay with
 * [RenderPassEncoder.executeBundles]. Obtained from
 * [RenderBundleEncoder.finish].
 */
@JvmInline
public expect value class RenderBundle internal constructor(
  internal val handle: WebGpuNativeHandle,
)
