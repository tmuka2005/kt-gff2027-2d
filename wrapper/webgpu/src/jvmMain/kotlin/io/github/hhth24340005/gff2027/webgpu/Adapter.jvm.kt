package io.github.hhth24340005.gff2027.webgpu

import java.lang.foreign.MemorySegment

public actual class Adapter internal constructor(
  internal val handle: MemorySegment,
  internal val instance: Instance,
)
