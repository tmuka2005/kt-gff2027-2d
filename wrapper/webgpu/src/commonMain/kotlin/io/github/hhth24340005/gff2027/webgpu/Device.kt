package io.github.hhth24340005.gff2027.webgpu

import arrow.AutoCloseScope

/**
 * A logical connection to an [Adapter] that creates GPU objects.
 * Obtained from [Adapter.requestDevice].
 */
public expect value class Device internal constructor(
  internal val handle: WebGpuNativeHandle,
  /** The queue that runs commands submitted to this device. */
  public val queue: Queue,
) {
  /**
   * Compiles [wgsl], a shader written in [WGSL](https://www.w3.org/TR/WGSL/).
   * The scope releases the module when it exits.
   */
  context(scope: AutoCloseScope)
  public fun createShaderModule(
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    wgsl: String,
    label: String? = null,
  ): ShaderModule

  /**
   * Creates a pipeline that draws triangle lists with one sample per pixel.
   */
  context(scope: AutoCloseScope)
  public fun createRenderPipeline(
    vertex: VertexState,
    fragment: FragmentState,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    label: String? = null,
  ): RenderPipeline

  /**
   * Records commands in [block], then runs them on the GPU. Runs nothing if
   * [block] throws or returns from an enclosing function.
   */
  public inline fun submitCommands(
    block: CommandEncoder.() -> Unit,
  )
}

// region internal

@PublishedApi
internal inline fun Device.submitCommandsImpl(
  block: CommandEncoder.() -> Unit,
) {
  val encoder = beginCommands()
  try {
    encoder.block()
    finishAndSubmit(encoder)
  } finally {
    encoder.release()
  }
}

@PublishedApi
internal expect fun Device.beginCommands(): CommandEncoder

@PublishedApi
internal expect fun Device.finishAndSubmit(
  encoder: CommandEncoder,
)

@PublishedApi
internal expect fun CommandEncoder.release()

// endregion
