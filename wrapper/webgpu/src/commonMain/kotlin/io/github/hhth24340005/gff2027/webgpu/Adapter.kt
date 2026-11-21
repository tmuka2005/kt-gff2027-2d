package io.github.hhth24340005.gff2027.webgpu

/**
 * A physical GPU and its driver. Obtained from [Instance.requestAdapter].
 */
public expect value class Adapter internal constructor(
  internal val handle: WebGpuNativeHandle,
  internal val instance: Instance,
)
