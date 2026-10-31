package io.github.hhth24340005.gff2027.webgpu

import java.lang.foreign.MemorySegment

@JvmInline
public actual value class PipelineLayout internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
) {
  public actual companion object {
    public actual val Auto: PipelineLayout = PipelineLayout(MemorySegment.NULL)
  }
}
