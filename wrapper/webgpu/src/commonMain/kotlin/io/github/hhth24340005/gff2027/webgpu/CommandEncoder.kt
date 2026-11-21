package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * Records GPU commands. Obtained from [Device.submitCommands].
 */
@JvmInline
public expect value class CommandEncoder internal constructor(
  internal val handle: WebGpuNativeHandle,
)
