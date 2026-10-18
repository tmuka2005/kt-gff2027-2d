package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import kotlin.jvm.JvmInline

/**
 * An image on the GPU. Obtained from [Device.createTexture] or
 * [Surface.getCurrentTexture].
 */
@JvmInline
public expect value class Texture internal constructor(
  internal val handle: WebGpuNativeHandle,
) {
  /**
   * Creates a view of the whole texture. The scope releases it when it exits.
   */
  context(scope: AutoCloseScope)
  public fun createView(): TextureView
}
