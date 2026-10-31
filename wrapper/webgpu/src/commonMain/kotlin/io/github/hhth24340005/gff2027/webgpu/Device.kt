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
   * Creates a layout of [entries] for bind groups. The scope releases it when
   * it exits.
   */
  context(scope: AutoCloseScope)
  public fun createBindGroupLayout(
    entries: List<BindGroupLayoutEntry>,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    label: String? = null,
  ): BindGroupLayout

  /**
   * Creates a pipeline layout whose bind group `i` follows
   * `bindGroupLayouts[i]`. The scope releases it when it exits.
   */
  context(scope: AutoCloseScope)
  public fun createPipelineLayout(
    bindGroupLayouts: List<BindGroupLayout>,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    label: String? = null,
  ): PipelineLayout

  /**
   * Creates a pipeline that draws triangle lists with one sample per pixel and
   * binds resources as [layout] declares. The scope releases it when it exits.
   * [PipelineLayout.Auto] derives the layout from the shaders, and
   * [RenderPipeline.getBindGroupLayout] returns its bind group layouts.
   */
  context(scope: AutoCloseScope)
  public fun createRenderPipeline(
    vertex: VertexState,
    fragment: FragmentState,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    layout: PipelineLayout = PipelineLayout.Auto,
    label: String? = null,
  ): RenderPipeline

  /**
   * Creates a buffer of [size] bytes that may be used in the ways of [usage].
   * The scope releases it when it exits.
   */
  context(scope: AutoCloseScope)
  public fun createBuffer(
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    size: Int,
    usage: Set<BufferUsage>,
    label: String? = null,
  ): Buffer

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
   * Creates a bind group of [entries] that matches [layout]. The scope releases
   * it when it exits.
   */
  context(scope: AutoCloseScope)
  public fun createBindGroup(
    layout: BindGroupLayout,
    entries: List<BindGroupEntry>,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    label: String? = null,
  ): BindGroup

  /**
   * Records commands in [block], then runs them on the GPU. Runs nothing if
   * [block] throws or returns from an enclosing function.
   */
  public inline fun submitCommands(
    block: CommandEncoder.() -> Unit,
  )

  /**
   * Creates an encoder that records draws into a [RenderBundle] for render
   * passes whose color attachments have [colorFormats] in order and one sample
   * per pixel. The scope releases it when it exits.
   */
  context(scope: AutoCloseScope)
  public fun createRenderBundleEncoder(
    colorFormats: List<TextureFormat>,
    @Suppress("UNUSED_PARAMETER") vararg namedOnly: NamedOnly,
    label: String? = null,
  ): RenderBundleEncoder
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
