package io.github.hhth24340005.gff2027.webgpu

import java.lang.foreign.MemorySegment

public actual class Sampler internal constructor(
  internal val handle: MemorySegment,
) : BindingResource

// region internal

// The entries follow WGPUFilterMode, which numbers them from 1 without gaps
internal val FilterMode.native: Int
  get() = ordinal + 1

// endregion
