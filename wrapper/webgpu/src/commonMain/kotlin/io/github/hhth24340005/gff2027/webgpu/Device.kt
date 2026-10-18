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
   * Creates a 2D texture of [size], given as width to height, that shaders can
   * sample and [Queue.writeTexture] can fill. The scope releases it when it
   * exits.
   */
  context(scope: AutoCloseScope)
  public fun createTexture(
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    size: Pair<Int, Int>,
    format: TextureFormat,
    label: String? = null,
  ): Texture

  /**
   * Creates a sampler that clamps coordinates outside a texture to its edge.
   * The scope releases it when it exits.
   *
   * @param magFilter the filter where a texel covers more than a pixel.
   * @param minFilter the filter where a texel covers less than a pixel.
   */
  context(scope: AutoCloseScope)
  public fun createSampler(
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    magFilter: FilterMode = FilterMode.Nearest,
    minFilter: FilterMode = FilterMode.Nearest,
    label: String? = null,
  ): Sampler

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
