package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope

/** The entry point to WebGPU. Created by calling `Instance()`. */
public expect class Instance

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
