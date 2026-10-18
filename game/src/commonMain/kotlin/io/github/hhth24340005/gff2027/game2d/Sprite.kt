package io.github.hhth24340005.gff2027.game2d

import arrow.AutoCloseScope
import arrow.autoCloseScope
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.fx.coroutines.resourceScope
import io.github.hhth24340005.gff2027.webgpu.BindGroup
import io.github.hhth24340005.gff2027.webgpu.BindGroupEntry
import io.github.hhth24340005.gff2027.webgpu.Buffer
import io.github.hhth24340005.gff2027.webgpu.Color
import io.github.hhth24340005.gff2027.webgpu.ColorTargetState
import io.github.hhth24340005.gff2027.webgpu.Device
import io.github.hhth24340005.gff2027.webgpu.FragmentState
import io.github.hhth24340005.gff2027.webgpu.Instance
import io.github.hhth24340005.gff2027.webgpu.LoadOp
import io.github.hhth24340005.gff2027.webgpu.RenderPassColorAttachment
import io.github.hhth24340005.gff2027.webgpu.RenderPipeline
import io.github.hhth24340005.gff2027.webgpu.Surface
import io.github.hhth24340005.gff2027.webgpu.TextureView
import io.github.hhth24340005.gff2027.webgpu.VertexState
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.ContinuationInterceptor
import kotlin.time.Duration.Companion.seconds

internal suspend fun drawSprite(
  instance: Instance,
  surface: Surface,
  size: Pair<Int, Int>,
  mainThread: ContinuationInterceptor,
  image: Image,
) {
  resourceScope {
    val adapter =
      instance.requestAdapter(surface) ?: error("No GPU adapter is available")
    val device = adapter.requestDevice()
    val format =
      surface.supportedFormats(adapter).firstOrNull()
        ?: error("The surface supports no format")
    withContext(mainThread) {
      surface.configure(device, format, size = size)
    }
    val module = device.createShaderModule(wgsl = SHADER)
    val pipeline =
      device.createRenderPipeline(
        VertexState(module),
        FragmentState(module, [ColorTargetState(format)]),
      )
    val bindGroup = spriteBindGroupOf(device, pipeline, image, size)

    while (true) {
      val _ =
        surface.useCurrentTexture { view ->
          device.draw(
            to = view,
            with = pipeline,
            bindGroup0 = bindGroup,
            vertexCount = 6,
          )
        }
      delay(1.seconds / 60)
    }
  }
}

internal value class Image(
  val size: Pair<Int, Int>,
  val argb: IntArray,
) {
  override fun equals(
    other: Any?,
  ): Boolean {
    if (other !is Image) {
      return false
    }
    if (size != other.size) {
      return false
    }
    if (!argb.contentEquals(other.argb)) {
      return false
    }

    return true
  }

  override fun hashCode(): Int {
    var result = size.hashCode()
    result = 31 * result + argb.contentHashCode()
    return result
  }
}

// region internal

context(_: AutoCloseScope)
private fun spriteBindGroupOf(
  device: Device,
  pipeline: RenderPipeline,
  image: Image,
  canvasSize: Pair<Int, Int>,
): BindGroup {
  val [width, _] = image.size
  val texture =
    device.createTexture(
      size = image.size,
      format = BGRA8UnormSrgb,
    )
  device.queue.writeTexture(texture, image.argb, elementsPerRow = width)
  val buf = bufferOfCenteredPlacement(device, canvasSize, image.size)

  return device.createBindGroup(
    pipeline.getBindGroupLayout(0),
    [
      BindGroupEntry(0, texture.createView()),
      BindGroupEntry(1, device.createSampler()),
      BindGroupEntry(2, buf),
    ],
  )
}

context(_: AutoCloseScope)
private fun bufferOfCenteredPlacement(
  device: Device,
  canvasSize: Pair<Int, Int>,
  imageSize: Pair<Int, Int>,
): Buffer {
  val [canvasWidth, canvasHeight] = canvasSize
  val [imageWidth, imageHeight] = imageSize
  val width = (imageWidth * SCALE).toFloat()
  val height = (imageHeight * SCALE).toFloat()
  val placement =
    floatArrayOf(
      canvasWidth.toFloat(),
      canvasHeight.toFloat(),
      (canvasWidth - width) / 2,
      (canvasHeight - height) / 2,
      width,
      height,
    )
  val buffer =
    device.createBuffer(
      size = placement.size * Float.SIZE_BYTES,
      usage = [Uniform, CopyDst],
    )
  device.queue.writeBuffer(buffer, placement)
  return buffer
}

private inline fun <R> Surface.useCurrentTexture(
  block: (TextureView) -> R,
): Option<R> =
  autoCloseScope {
    val current = getCurrentTexture()
    if (current !is Success) {
      return None
    }
    val view = current.texture.createView()
    val ret = block(view)
    present()
    Some(ret)
  }

private fun Device.draw(
  to: TextureView,
  with: RenderPipeline,
  bindGroup0: BindGroup,
  vertexCount: Int,
) {
  submitCommands {
    renderPass(
      [RenderPassColorAttachment(to, LoadOp.Clear(BACKGROUND))],
    ) {
      setPipeline(with)
      setBindGroup(0, bindGroup0)
      draw(vertexCount = vertexCount)
    }
  }
}

private val BACKGROUND = Color(red = 0.1, green = 0.1, blue = 0.15)

private const val SCALE = 16

private const val SHADER =
  """
  struct Placement {
    canvas_size: vec2f,
    // The bottom-left corner in pixels, with y pointing up
    position: vec2f,
    size: vec2f,
  }

  @group(0) @binding(0) var sprite: texture_2d<f32>;
  @group(0) @binding(1) var sprite_sampler: sampler;
  @group(0) @binding(2) var<uniform> placement: Placement;

  struct VertexOutput {
    @builtin(position) position: vec4f,
    @location(0) uv: vec2f,
  }

  @vertex
  fn vs_main(@builtin(vertex_index) i: u32) -> VertexOutput {
    let corners = array(
      vec2f(0.0, 0.0), vec2f(1.0, 0.0), vec2f(0.0, 1.0),
      vec2f(0.0, 1.0), vec2f(1.0, 0.0), vec2f(1.0, 1.0),
    );
    let corner = corners[i];
    let pixel = placement.position + corner * placement.size;
    let position = normalize_to_clip(pixel, placement.canvas_size);
    // uv points y down, as images list their rows top first
    let uv = vec2f(corner.x, 1.0 - corner.y);
    return VertexOutput(vec4f(position, 0.0, 1.0), uv);
  }

  // Normalizes pixel from [0, canvas_size] to [-1, 1]
  fn normalize_to_clip(pixel: vec2f, canvas_size: vec2f) -> vec2f {
    return pixel / canvas_size * 2.0 - 1.0;
  }

  @fragment
  fn fs_main(@location(0) uv: vec2f) -> @location(0) vec4f {
    let color = textureSample(sprite, sprite_sampler, uv);
    if color.a < 0.5 {
      discard;
    }
    return color;
  }
  """.trimIndent()

// endregion
