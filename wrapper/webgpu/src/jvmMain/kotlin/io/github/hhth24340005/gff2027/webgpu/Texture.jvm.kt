package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import wgpu_native.webgpu_h.wgpuTextureCreateView
import wgpu_native.webgpu_h.wgpuTextureRelease
import wgpu_native.webgpu_h.wgpuTextureViewRelease
import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySegment.NULL

public actual class Texture internal constructor(
  internal val handle: MemorySegment,
) {
  context(scope: AutoCloseScope)
  public actual fun createView(): TextureView =
    scope.autoClose(
      acquire = { createViewImpl() },
      release = { view, _ -> view.release() },
    )
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
