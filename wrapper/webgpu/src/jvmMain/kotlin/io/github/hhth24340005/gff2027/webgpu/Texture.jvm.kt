package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.webgpu_h.wgpuTextureCreateView
import wgpu_native.webgpu_h.wgpuTextureRelease
import wgpu_native.webgpu_h.wgpuTextureViewRelease
import java.lang.foreign.MemorySegment.NULL

@JvmInline
public actual value class Texture internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
) {
  context(scope: AutoCloseScope)
  public actual fun createView(): TextureView =
    createViewImpl().also { view ->
      scope.onClose { view.release() }
    }
}

// region internal

internal fun Texture.release() {
  wgpuTextureRelease(handle)
}

private fun Texture.createViewImpl(): TextureView =
  TextureView(wgpuTextureCreateView(handle, NULL))

private fun TextureView.release() {
  wgpuTextureViewRelease(handle)
}

// endregion
