package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import arrow.fx.coroutines.ResourceScope
import io.github.hhth24340005.gff2027.window.Window
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import wgpu_native.WGPUChainedStruct
import wgpu_native.WGPURequestAdapterCallback
import wgpu_native.WGPURequestAdapterCallbackInfo
import wgpu_native.WGPURequestAdapterOptions
import wgpu_native.WGPUSurfaceDescriptor
import wgpu_native.WGPUSurfaceSourceMetalLayer
import wgpu_native.WGPUSurfaceSourceWaylandSurface
import wgpu_native.WGPUSurfaceSourceWindowsHWND
import wgpu_native.WGPUSurfaceSourceXlibWindow
import wgpu_native.webgpu_h.WGPUCallbackMode_AllowSpontaneous
import wgpu_native.webgpu_h.WGPURequestAdapterStatus_CallbackCancelled
import wgpu_native.webgpu_h.WGPURequestAdapterStatus_Success
import wgpu_native.webgpu_h.WGPURequestAdapterStatus_Unavailable
import wgpu_native.webgpu_h.WGPUSType_SurfaceSourceMetalLayer
import wgpu_native.webgpu_h.WGPUSType_SurfaceSourceWaylandSurface
import wgpu_native.webgpu_h.WGPUSType_SurfaceSourceWindowsHWND
import wgpu_native.webgpu_h.WGPUSType_SurfaceSourceXlibWindow
import wgpu_native.webgpu_h.wgpuAdapterRelease
import wgpu_native.webgpu_h.wgpuCreateInstance
import wgpu_native.webgpu_h.wgpuInstanceCreateSurface
import wgpu_native.webgpu_h.wgpuInstanceProcessEvents
import wgpu_native.webgpu_h.wgpuInstanceRelease
import wgpu_native.webgpu_h.wgpuInstanceRequestAdapter
import wgpu_native.webgpu_h.wgpuSurfaceRelease
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment
import java.lang.foreign.MemorySegment.NULL

@JvmInline
public actual value class Instance internal actual constructor(
  internal actual val handle: WebGpuNativeHandle,
) {
  context(scope: ResourceScope)
  public actual suspend fun requestAdapter(
    compatibleSurface: Surface?,
  ): Adapter? =
    scope.install(
      acquire = { requestAdapterImpl(compatibleSurface) },
      release = { adapter, _ -> adapter?.release() },
    )

  /**
   * Creates a surface on [window]. Fails for [Window.Offscreen], which has no
   * native surface.
   */
  context(scope: AutoCloseScope)
  public fun createSurface(
    window: Window,
  ): Surface =
    createSurfaceImpl(window).also { surface ->
      scope.onClose { surface.release() }
    }
}

// region internal

internal actual fun createInstance(): Instance =
  Instance(wgpuCreateInstance(NULL))

internal actual fun Instance.release() {
  wgpuInstanceRelease(handle)
}

@OptIn(ExperimentalCoroutinesApi::class)
internal suspend fun <T> Instance.awaitCallback(
  release: (T) -> Unit,
  request: (CompletableDeferred<T>) -> Unit,
): T {
  val result = CompletableDeferred<T>()
  request(result)
  try {
    pumpUntil(result)
  } catch (e: CancellationException) {
    withContext(NonCancellable) { pumpUntil(result) }
    if (result.getCompletionExceptionOrNull() == null) {
      release(result.getCompleted())
    }
    throw e
  }
  return result.getCompleted()
}

private suspend fun Instance.requestAdapterImpl(
  compatibleSurface: Surface?,
): Adapter? =
  Arena.ofShared().use { arena ->
    awaitCallback(release = { it?.release() }) { adapter ->
      wgpuInstanceRequestAdapter(
        arena,
        handle,
        requestAdapterOptions(arena, compatibleSurface),
        requestAdapterCallbackInfo(arena, adapter),
      )
    }
  }

private fun Adapter.release() {
  wgpuAdapterRelease(handle)
}

private suspend fun Instance.pumpUntil(
  callback: Deferred<*>,
) {
  while (!callback.isCompleted) {
    wgpuInstanceProcessEvents(handle)
    yield()
  }
}

private fun requestAdapterOptions(
  arena: Arena,
  compatibleSurface: Surface?,
): MemorySegment {
  val options = WGPURequestAdapterOptions.allocate(arena)
  WGPURequestAdapterOptions
    .compatibleSurface(options, compatibleSurface?.handle ?: NULL)
  return options
}

private fun Instance.requestAdapterCallbackInfo(
  arena: Arena,
  adapter: CompletableDeferred<Adapter?>,
): MemorySegment {
  val callback =
    WGPURequestAdapterCallback.allocate(
      { status, result, message, _, _ ->
        when (status) {
          WGPURequestAdapterStatus_Success() -> {
            adapter.complete(Adapter(result, this))
          }

          WGPURequestAdapterStatus_Unavailable() -> {
            adapter.complete(null)
          }

          // Only when the instance is released before the callback, which
          // the resource scope prevents by releasing it after the adapter
          WGPURequestAdapterStatus_CallbackCancelled() -> {
            adapter.completeExceptionally(
              IllegalStateException(
                "Adapter request was cancelled: the instance was released",
              ),
            )
          }

          else -> {
            adapter.completeExceptionally(
              IllegalStateException(
                "Adapter request failed: ${message.stringView()}",
              ),
            )
          }
        }
      },
      arena,
    )
  val info = WGPURequestAdapterCallbackInfo.allocate(arena)
  WGPURequestAdapterCallbackInfo
    .mode(info, WGPUCallbackMode_AllowSpontaneous())
  WGPURequestAdapterCallbackInfo.callback(info, callback)
  return info
}

private fun Instance.createSurfaceImpl(
  window: Window,
): Surface =
  Arena.ofConfined().use { arena ->
    Surface(
      wgpuInstanceCreateSurface(handle, surfaceDescriptor(arena, window)),
    )
  }

private fun surfaceDescriptor(
  arena: Arena,
  window: Window,
): MemorySegment {
  val descriptor = WGPUSurfaceDescriptor.allocate(arena)
  WGPUSurfaceDescriptor.nextInChain(descriptor, window.source(arena))
  return descriptor
}

private fun Window.source(
  arena: Arena,
): MemorySegment =
  when (this) {
    is Cocoa -> {
      WGPUSurfaceSourceMetalLayer.allocate(arena).also {
        WGPUChainedStruct.sType(
          WGPUSurfaceSourceMetalLayer.chain(it),
          WGPUSType_SurfaceSourceMetalLayer(),
        )
        WGPUSurfaceSourceMetalLayer
          .layer(it, MemorySegment.ofAddress(metalLayer))
      }
    }

    is Win32 -> {
      WGPUSurfaceSourceWindowsHWND.allocate(arena).also {
        WGPUChainedStruct.sType(
          WGPUSurfaceSourceWindowsHWND.chain(it),
          WGPUSType_SurfaceSourceWindowsHWND(),
        )
        WGPUSurfaceSourceWindowsHWND
          .hinstance(it, MemorySegment.ofAddress(hinstance))
        WGPUSurfaceSourceWindowsHWND.hwnd(it, MemorySegment.ofAddress(hwnd))
      }
    }

    is X11 -> {
      WGPUSurfaceSourceXlibWindow.allocate(arena).also {
        WGPUChainedStruct.sType(
          WGPUSurfaceSourceXlibWindow.chain(it),
          WGPUSType_SurfaceSourceXlibWindow(),
        )
        WGPUSurfaceSourceXlibWindow
          .display(it, MemorySegment.ofAddress(display))
        WGPUSurfaceSourceXlibWindow.window(it, window)
      }
    }

    is Wayland -> {
      WGPUSurfaceSourceWaylandSurface.allocate(arena).also {
        WGPUChainedStruct.sType(
          WGPUSurfaceSourceWaylandSurface.chain(it),
          WGPUSType_SurfaceSourceWaylandSurface(),
        )
        WGPUSurfaceSourceWaylandSurface
          .display(it, MemorySegment.ofAddress(display))
        WGPUSurfaceSourceWaylandSurface
          .surface(it, MemorySegment.ofAddress(surface))
      }
    }

    is Offscreen -> {
      error("The offscreen driver has no surface to draw on")
    }
  }

private fun Surface.release() {
  wgpuSurfaceRelease(handle)
}

// endregion
