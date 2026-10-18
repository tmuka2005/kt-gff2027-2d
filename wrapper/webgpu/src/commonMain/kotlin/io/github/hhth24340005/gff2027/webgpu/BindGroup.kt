package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * Resources that shaders read together. Obtained from
 * [Device.createBindGroup].
 */
@JvmInline
public expect value class BindGroup internal constructor(
  internal val handle: WebGpuNativeHandle,
)

/** Binds [resource] to `@binding(binding)` in a [BindGroup]. */
public value class BindGroupEntry(
  public val binding: Int,
  public val resource: BindingResource,
)

/** A resource that a [BindGroupEntry] binds. */
public expect sealed interface BindingResource
