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
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
fun essa2() {
    val size = 30
    val engine = prepareEngine(size)
    val running: MutableState<Boolean> = mutableStateOf(false)
    sleep(2000)
    CoroutineScope(Dispatchers.Default).launch {
        runSimulation(running, 200, engine)
    }

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

//                val test: MainGrid =
//                    MainGrid(WallWorldMap(emptyList(), Jungle(Vector2d(0, 0), Vector2d(1, 10)), 20, 20))
//                test.getView()
            }
            Column {
                Text("essa")
                LazyVerticalGrid(cells = GridCells.Fixed(size)) {
                    items(size * size) {
                        val x = it / size
                        val y = it % size
                        val a = GridCell(
                            engine.map.getViewObj(Vector2d(x, y)),
                            engine.map,
                            x,
                            y,
                            engine.map.returnBackGroundColor(Vector2d(x, y))
                        )
                        a.getView()
                    }
                }
            }
        }
    }
}

fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Simulation",
            state = rememberWindowState(
                position = WindowPosition(alignment = Alignment.Center),
            ),
        ) {
            essa2()
        }

    }
}