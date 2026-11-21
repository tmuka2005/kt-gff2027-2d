@file:Suppress("MissingPackageDeclaration")

import arrow.autoCloseScope
import arrow.fx.coroutines.ResourceScope
import io.github.hhth24340005.gff2027.webgpu.Color
import io.github.hhth24340005.gff2027.webgpu.ColorTargetState
import io.github.hhth24340005.gff2027.webgpu.Device
import io.github.hhth24340005.gff2027.webgpu.FragmentEntryPoint
import io.github.hhth24340005.gff2027.webgpu.FragmentState
import io.github.hhth24340005.gff2027.webgpu.Instance
import io.github.hhth24340005.gff2027.webgpu.LoadOp
import io.github.hhth24340005.gff2027.webgpu.RenderPassColorAttachment
import io.github.hhth24340005.gff2027.webgpu.RenderPipeline
import io.github.hhth24340005.gff2027.webgpu.Surface
import io.github.hhth24340005.gff2027.webgpu.VertexEntryPoint
import io.github.hhth24340005.gff2027.webgpu.VertexState
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration.Companion.seconds

/**
 * Draws a triangle on [surface] every frame until canceled. Configures
 * [surface] on [windowContext], as some platforms allow changing a window only
 * from its thread.
 */
context(_: ResourceScope)
internal suspend fun drawTriangle(
  instance: Instance,
  surface: Surface,
  size: Pair<Int, Int>,
  windowContext: CoroutineContext,
) {
  val adapter =
    instance.requestAdapter(surface) ?: error("No GPU adapter is available")
  val device = adapter.requestDevice()
  val format =
    surface.supportedFormats(adapter).firstOrNull()
      ?: error("The surface supports no format")
  withContext(windowContext) {
    surface.configure(device, format, size = size)
  }
  val module = device.createShaderModule(wgsl = SHADER)
  val pipeline =
    device.createRenderPipeline(
      VertexState(module, entryPoint = VertexEntryPoint("vs_main")),
      FragmentState(
        module,
        [ColorTargetState(format)],
        entryPoint = FragmentEntryPoint("fs_main"),
      ),
    )

  while (true) {
    drawFrame(surface, device, pipeline)
    delay(1.seconds / 60)
  }
}

private fun drawFrame(
  surface: Surface,
  device: Device,
  pipeline: RenderPipeline,
) {
  autoCloseScope {
    val current = surface.getCurrentTexture()
    if (current !is Success) {
      return
    }
    val view = current.texture.createView()
    val encoder = device.createCommandEncoder()
    encoder.renderPass(
      [RenderPassColorAttachment(view, LoadOp.Clear(BACKGROUND))],
    ) {
      setPipeline(pipeline)
      draw(vertexCount = 3)
    }
    device.queue.submit([encoder.finish()])
  }
  surface.present()
}

private val BACKGROUND = Color(red = 0.1, green = 0.1, blue = 0.15)

private const val SHADER =
  """
  @vertex
  fn vs_main(@builtin(vertex_index) i: u32) -> @builtin(position) vec4f {
    let positions = array(vec2f(0.0, 0.5), vec2f(-0.5, -0.5), vec2f(0.5, -0.5));
    return vec4f(positions[i], 0.0, 1.0);
  }

  @fragment
  fn fs_main() -> @location(0) vec4f {
    return vec4f(1.0, 0.4, 0.2, 1.0);
  }
  """.trimIndent()
