package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope

/**
 * A target that presents rendered images to a window. Obtained from
 * `Instance.createSurface`, which each platform defines for its windows.
 */
public expect class Surface {
  /**
   * Lists the formats [adapter] can present to this surface, preferred first.
   */
  public fun supportedFormats(
    adapter: Adapter,
  ): List<TextureFormat>

  /**
   * Prepares this surface to present images of [format] and [size], given as
   * width to height, rendered by [device]. Call it again when the window is
   * resized.
   */
  public fun configure(
    device: Device,
    format: TextureFormat,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    size: Pair<Int, Int>,
  )

  /**
   * Returns the texture to draw the next frame to, or why there is none. Fails
   * when the surface needs [configure] again.
   */
  context(scope: AutoCloseScope)
  public fun getCurrentTexture(): CurrentTexture

  /** Shows the texture from [getCurrentTexture] on the window. */
  public fun present()
}

public sealed interface CurrentTexture {
  /** [texture] receives the next frame. */
  public class Success(public val texture: Texture) : CurrentTexture

  /**
   * The window is hidden. Trying again returns immediately until it shows,
   * instead of waiting for the display.
   */
  public data object Occluded : CurrentTexture

  /** No texture became free in time. Trying again may succeed. */
  public data object Timeout : CurrentTexture
}
