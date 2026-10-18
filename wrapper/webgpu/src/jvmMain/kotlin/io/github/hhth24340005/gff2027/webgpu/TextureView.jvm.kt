package io.github.hhth24340005.gff2027.webgpu

import java.lang.foreign.MemorySegment

public actual class TextureView internal constructor(
  internal val handle: MemorySegment,
) : BindingResource
