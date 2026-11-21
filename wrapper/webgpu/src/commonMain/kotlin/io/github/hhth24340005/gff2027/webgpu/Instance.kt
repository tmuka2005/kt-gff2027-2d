package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope
import kotlin.jvm.JvmInline

/** The entry point to WebGPU. Created by calling `Instance()`. */
@JvmInline
public expect value class Instance internal constructor(
  internal val handle: WebGpuNativeHandle,
)

/** Creates an instance. The scope releases it when it exits. */
context(scope: AutoCloseScope)
public fun Instance(): Instance =
  createInstance().also { instance ->
    scope.onClose { instance.release() }
  }

// region internal

internal expect fun createInstance(): Instance

internal expect fun Instance.release()

// endregion
