package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * A logical connection to an [Adapter] that creates GPU objects.
 * Obtained from [Adapter.requestDevice].
 */
@JvmInline
public expect value class Device internal constructor(
  internal val handle: WebGpuNativeHandle,
)
