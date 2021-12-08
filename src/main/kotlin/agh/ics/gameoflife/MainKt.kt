package agh.ics.gameoflife

import agh.ics.gameoflife.engine.IEngine
import agh.ics.gameoflife.engine.MagicEngine
import agh.ics.gameoflife.map.WrappedWorldMap
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.regions.Jungle
import agh.ics.gameoflife.view.GridCell
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep

fun prepareEngine(size: Int): IEngine {
    val engine = MagicEngine(
        WrappedWorldMap(emptyList(), Jungle(Vector2d(5, 5), Vector2d(20, 20)), size, size),
        3
    )
    engine.placeAnimals(80, 100)
    return engine
}

fun runSimulation(running: MutableState<Boolean>, time: Int, engine: IEngine) {
    while (true) {
        sleep(time.toLong())
        if (running.value) {
            engine.runIteration()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun getMainView(running: MutableState<Boolean>) {
    val size = 30
    val engine = prepareEngine(size)
    sleep(2000)
    CoroutineScope(Dispatchers.Default).launch {
        runSimulation(running, 200, engine)
    }

    val img = painterResource("animal.png")
    MaterialTheme {
        Row {
            Column {
                Button(
                    onClick = {
                        running.value = !running.value
                    }) {

                    val start = "Start"
                    val stop = "Stop"
                    Text("${if (running.value) stop else start} simulation")
                }

            }
            Column(modifier = Modifier.size(((size + 1) * 20).dp)) {
                val siz2 = size + 1
                Text("essa")
                LazyVerticalGrid(cells = GridCells.Fixed(siz2)) {
                    items(siz2 * siz2) {
                        val x = it / siz2
                        val y = it % siz2
                        val a = GridCell(
                            engine.map.getViewObj(Vector2d(x, y)),
                            engine.map,
                            x,
                            y,
                            engine.map.returnBackGroundColor(Vector2d(x, y))
                        )
                        a.getView(img)
                    }
                }
            }
        }
    }
}

fun main() {
    lateinit var running: MutableState<Boolean>
    runBlocking {
        CoroutineScope(Dispatchers.Default).launch {
            running = mutableStateOf(false)
        }.join()
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Simulation",
            state = rememberWindowState(
                position = WindowPosition(alignment = Alignment.Center),
            )
        ) {
            getMainView(running)
        }

    }
}