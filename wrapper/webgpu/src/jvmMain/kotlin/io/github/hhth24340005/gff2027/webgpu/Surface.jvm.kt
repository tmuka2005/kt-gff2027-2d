package io.github.hhth24340005.gff2027.webgpu

import wgpu_native.WGPUSurfaceCapabilities
import wgpu_native.WGPUSurfaceConfiguration
import wgpu_native.webgpu_h.WGPUStatus_Success
import wgpu_native.webgpu_h.wgpuSurfaceCapabilitiesFreeMembers
import wgpu_native.webgpu_h.wgpuSurfaceConfigure
import wgpu_native.webgpu_h.wgpuSurfaceGetCapabilities
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.ValueLayout.JAVA_INT

/**
 * A target that presents rendered images to a window. Obtained from
 * [Instance.createSurface].
 */
public actual class Surface internal constructor(
  internal val handle: MemorySegment,
) {
  internal var configured: Boolean = false

  public actual fun supportedFormats(
    adapter: Adapter,
  ): List<TextureFormat> =
    supportedFormatsImpl(adapter)

  public actual fun configure(
    device: Device,
    format: TextureFormat,
    @Suppress(names = ["UNUSED_PARAMETER"]) vararg namedOnly: NamedOnly,
    size: Pair<Int, Int>,
  ) {
    val [width, height] = size
    configureImpl(device, format, width, height)
  }
}

// region internal

private fun Surface.supportedFormatsImpl(
  adapter: Adapter,
): List<TextureFormat> =
  Arena.ofConfined().use { arena ->
    val capabilities = WGPUSurfaceCapabilities.allocate(arena)
    // Fails only for an unknown struct in capabilities.nextInChain, which
    // stays null here
    check(
      wgpuSurfaceGetCapabilities(handle, adapter.handle, capabilities) ==
        WGPUStatus_Success(),
    ) { "wgpuSurfaceGetCapabilities rejected the chain in its output struct" }
    try {
      capabilities.formats().mapNotNull(::textureFormatOf)
    } finally {
      wgpuSurfaceCapabilitiesFreeMembers(capabilities)
    }
  }

private fun Surface.configureImpl(
  device: Device,
  format: TextureFormat,
  width: Int,
  height: Int,
) {
  Arena.ofConfined().use { arena ->
    wgpuSurfaceConfigure(
      handle,
      surfaceConfiguration(arena, device, format, width, height),
    )
  }
  configured = true
}

private fun MemorySegment.formats(): List<Int> {
  val count = WGPUSurfaceCapabilities.formatCount(this)
  val formats =
    WGPUSurfaceCapabilities
      .formats(this)
      .reinterpret(count * JAVA_INT.byteSize())
  return List(count.toInt()) { formats.getAtIndex(JAVA_INT, it.toLong()) }
}

private fun surfaceConfiguration(
  arena: Arena,
  device: Device,
  format: TextureFormat,
  width: Int,
  height: Int,
): MemorySegment {
  val configuration = WGPUSurfaceConfiguration.allocate(arena)
  WGPUSurfaceConfiguration.device(configuration, device.handle)
  WGPUSurfaceConfiguration.format(configuration, format.native)
  WGPUSurfaceConfiguration
    .usage(configuration, TEXTURE_USAGE_RENDER_ATTACHMENT)
  WGPUSurfaceConfiguration.width(configuration, width)
  WGPUSurfaceConfiguration.height(configuration, height)
  return configuration
}

// jextract skips it, as webgpu.h declares it as static const
private const val TEXTURE_USAGE_RENDER_ATTACHMENT = 0x10L

// endregion
