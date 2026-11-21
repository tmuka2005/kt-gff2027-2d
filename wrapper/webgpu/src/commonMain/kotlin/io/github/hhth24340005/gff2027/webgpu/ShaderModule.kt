package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/** A compiled shader. Obtained from [Device.createShaderModule]. */
@JvmInline
public expect value class ShaderModule internal constructor(
  internal val handle: WebGpuNativeHandle,
)
