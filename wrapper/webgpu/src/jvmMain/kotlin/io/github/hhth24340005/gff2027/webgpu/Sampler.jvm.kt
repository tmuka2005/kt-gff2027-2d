package io.github.hhth24340005.gff2027.webgpu

@JvmInline
public actual value class Sampler internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
) : BindingResource

// region internal

// The entries follow WGPUFilterMode, which numbers them from 1 without gaps
internal val FilterMode.native: Int
  get() = ordinal + 1

// endregion
