package io.github.hhth24340005.gff2027.webgpu

import kotlin.jvm.JvmInline

/**
 * A queue that runs commands on a [Device]. Obtained from [Device.queue], and
 * released with the device.
 */
@JvmInline
public expect value class Queue internal constructor(
  internal val handle: WebGpuNativeHandle,
) {
  /** Copies [data] into [buffer] from its start. */
  public fun writeBuffer(
    buffer: Buffer,
    data: FloatArray,
  )

  /**
   * Copies [data] into [buffer] from its start. Each element lies in the
   * platform's byte order, which is little-endian on every supported platform,
   * as WGSL reads it.
   */
  public fun writeBuffer(
    buffer: Buffer,
    data: IntArray,
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
