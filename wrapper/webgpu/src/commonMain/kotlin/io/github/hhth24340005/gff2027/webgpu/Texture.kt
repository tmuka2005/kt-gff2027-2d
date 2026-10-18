package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope

/**
 * An image on the GPU. Obtained from [Device.createTexture] or
 * [Surface.getCurrentTexture].
 */
public expect class Texture {
  /**
   * Creates a view of the whole texture. The scope releases it when it exits.
   */
  context(scope: AutoCloseScope)
  public fun createView(): TextureView
}
