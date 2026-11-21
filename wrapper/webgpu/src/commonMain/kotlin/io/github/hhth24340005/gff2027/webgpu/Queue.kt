package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * A queue that runs commands on a [Device]. Obtained from [Device.queue], and
 * released with the device.
 */
@JvmInline
public expect value class Queue internal constructor(
  internal val handle: WebGpuNativeHandle,
)
