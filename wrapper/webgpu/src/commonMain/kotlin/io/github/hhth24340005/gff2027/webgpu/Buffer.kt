package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/** Memory on the GPU that shaders read. Obtained from [Device.createBuffer]. */
@JvmInline
public expect value class Buffer internal constructor(
  internal val handle: WebGpuNativeHandle,
) : BindingResource
