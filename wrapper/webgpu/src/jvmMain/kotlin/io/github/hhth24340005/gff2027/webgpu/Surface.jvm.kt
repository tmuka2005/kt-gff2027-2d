package io.github.hhth24340005.gff2027.webgpu

import java.lang.foreign.MemorySegment

/**
 * A target that presents rendered images to a window. Obtained from
 * [Instance.createSurface].
 */
public actual class Surface internal constructor(
  internal val handle: MemorySegment,
)
