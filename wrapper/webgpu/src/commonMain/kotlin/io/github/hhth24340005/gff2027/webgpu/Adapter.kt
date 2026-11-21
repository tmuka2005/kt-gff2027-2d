package io.github.hhth24340005.gff2027.webgpu

import arrow.fx.coroutines.ResourceScope

/**
 * A physical GPU and its driver. Obtained from [Instance.requestAdapter].
 */
public expect value class Adapter internal constructor(
  internal val handle: WebGpuNativeHandle,
  internal val instance: Instance,
) {
  /** Requests a device. The scope releases it when it exits. */
  context(scope: ResourceScope)
  public suspend fun requestDevice(
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    label: String? = null,
  ): Device
}
