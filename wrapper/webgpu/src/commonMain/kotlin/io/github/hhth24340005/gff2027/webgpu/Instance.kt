package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import arrow.fx.coroutines.ResourceScope

/** The entry point to WebGPU. Created by calling `Instance()`. */
public expect class Instance {
  /**
   * Requests an adapter. If [compatibleSurface] is given, the adapter can
   * present to it. Returns `null` if no adapter matches.
   */
  context(scope: ResourceScope)
  public suspend fun requestAdapter(
    compatibleSurface: Surface? = null,
  ): Adapter?
}

/** Creates an instance. The scope releases it when it exits. */
context(scope: AutoCloseScope)
public fun Instance(): Instance =
  scope.autoClose(
    acquire = { createInstance() },
    release = { instance, _ -> instance.release() },
  )

// region internal

internal expect fun createInstance(): Instance

internal expect fun Instance.release()

// endregion
