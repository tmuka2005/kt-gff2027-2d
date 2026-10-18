@file:Suppress("MissingPackageDeclaration")

import arrow.fx.coroutines.resourceScope
import io.github.hhth24340005.gff2027.webgpu.Instance
import io.github.hhth24340005.gff2027.window.CloseRequests
import io.github.hhth24340005.gff2027.window.Window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

public fun main(): Unit =
  runBlocking {
    val window =
      Window
        .createOnCurrentDispatcher(title = "Triangle", size = WIDTH to HEIGHT)
    val closeRequests = CloseRequests()
    window.use(closeRequests) {
      val mainThread = coroutineContext.minusKey(Job)
      val game =
        launch(Dispatchers.Default) {
          resourceScope {
            coroutineScope {
              val instance = Instance()
              drawTriangle(
                instance,
                instance.createSurface(window),
                WIDTH to HEIGHT,
                mainThread,
              )
            }
          }
        }
      closeRequests.receive()
      game.cancelAndJoin()
    }
  }

// region internal

private const val WIDTH = 640
private const val HEIGHT = 480

// endregion
