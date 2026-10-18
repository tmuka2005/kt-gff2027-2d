package io.github.hhth24340005.gff2027.webgpu

/**
 * A queue that runs commands on a [Device]. Obtained from [Device.queue], and
 * released with the device.
 */
public expect class Queue {
  /** Runs [commands] on the GPU in order. */
  public fun submit(
    commands: List<CommandBuffer>,
  )

  /**
   * Copies [data] into the whole [texture], whose rows each start
   * [elementsPerRow] elements after the previous one, top row first. Each
   * element lies in the platform's byte order, which is little-endian on every
   * supported platform, so an ARGB element fills a [TextureFormat.BGRA8Unorm]
   * texel.
   */
  public fun writeTexture(
    texture: Texture,
    data: IntArray,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    elementsPerRow: Int,
  )
}
