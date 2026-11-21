package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.WGPUSurfaceCapabilities
import wgpu_native.WGPUSurfaceConfiguration
import wgpu_native.WGPUSurfaceTexture
import wgpu_native.webgpu_h.WGPUStatus_Success
import wgpu_native.webgpu_h.WGPUSurfaceGetCurrentTextureStatus_Lost
import wgpu_native.webgpu_h.WGPUSurfaceGetCurrentTextureStatus_Outdated
import wgpu_native.webgpu_h.WGPUSurfaceGetCurrentTextureStatus_SuccessOptimal
import wgpu_native.webgpu_h.WGPUSurfaceGetCurrentTextureStatus_SuccessSuboptimal
import wgpu_native.webgpu_h.WGPUSurfaceGetCurrentTextureStatus_Timeout
import wgpu_native.webgpu_h.wgpuSurfaceCapabilitiesFreeMembers
import wgpu_native.webgpu_h.wgpuSurfaceConfigure
import wgpu_native.webgpu_h.wgpuSurfaceGetCapabilities
import wgpu_native.webgpu_h.wgpuSurfaceGetCurrentTexture
import wgpu_native.webgpu_h.wgpuSurfacePresent
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

  context(scope: AutoCloseScope)
  public actual fun getCurrentTexture(): CurrentTexture =
    scope.autoClose(
      acquire = { getCurrentTextureImpl() },
      release = { texture, _ -> texture.release() },
    )

  public actual fun present() {
    presentImpl()
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

private fun Surface.getCurrentTextureImpl(): CurrentTexture =
  Arena.ofConfined().use { arena ->
    val surfaceTexture = WGPUSurfaceTexture.allocate(arena)
    wgpuSurfaceGetCurrentTexture(handle, surfaceTexture)
    val texture = WGPUSurfaceTexture.texture(surfaceTexture)
    when (val status = WGPUSurfaceTexture.status(surfaceTexture)) {
      WGPUSurfaceGetCurrentTextureStatus_SuccessOptimal(),
      WGPUSurfaceGetCurrentTextureStatus_SuccessSuboptimal(),
      -> {
        CurrentTexture.Success(Texture(texture))
      }

      WGPUSurfaceGetCurrentTextureStatus_Timeout() -> {
        Timeout
      }

      SURFACE_TEXTURE_STATUS_OCCLUDED -> {
        Occluded
      }

      WGPUSurfaceGetCurrentTextureStatus_Outdated(),
      WGPUSurfaceGetCurrentTextureStatus_Lost(),
      -> {
        error("The surface needs configure again (status $status)")
      }

      // Only when the surface is not configured
      else -> {
        error("wgpuSurfaceGetCurrentTexture failed with status $status")
      }
    }
  }

private fun Surface.presentImpl() {
  // Fails only when the surface is not configured
  check(wgpuSurfacePresent(handle) == WGPUStatus_Success()) {
    "wgpuSurfacePresent failed: the surface is not configured"
  }
}

private fun CurrentTexture.release() {
  if (this is Success) {
    texture.release()
  }
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

// From wgpu.h, which is not bound. Metal reports it while the window is hidden
private const val SURFACE_TEXTURE_STATUS_OCCLUDED = 0x00030001

// endregion
