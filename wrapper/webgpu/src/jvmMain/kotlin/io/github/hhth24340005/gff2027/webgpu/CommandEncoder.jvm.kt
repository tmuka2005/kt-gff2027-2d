package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.webgpu_h.wgpuCommandBufferRelease
import wgpu_native.webgpu_h.wgpuCommandEncoderFinish
import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySegment.NULL

public actual class CommandEncoder internal constructor(
  internal val handle: MemorySegment,
) {
  context(scope: AutoCloseScope)
  public actual fun finish(): CommandBuffer =
    scope.autoClose(
      acquire = { finishImpl() },
      release = { commands, _ -> commands.release() },
    )
}

// region internal

private fun CommandEncoder.finishImpl(): CommandBuffer =
  CommandBuffer(wgpuCommandEncoderFinish(handle, NULL))

private fun CommandBuffer.release() {
  wgpuCommandBufferRelease(handle)
}

// endregion
