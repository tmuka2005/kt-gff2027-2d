package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope

/**
 * Records GPU commands into a [CommandBuffer]. Obtained from
 * [Device.createCommandEncoder].
 */
public expect class CommandEncoder {
  /** Finishes recording. The encoder cannot record after this. */
  context(scope: AutoCloseScope)
  public fun finish(): CommandBuffer
}
