package agh.ics.gameoflife

import agh.ics.gameoflife.engine.IEngine
import agh.ics.gameoflife.engine.MagicEngine
import agh.ics.gameoflife.engine.PlainEngine
import agh.ics.gameoflife.map.WallWorldMap
import agh.ics.gameoflife.map.WrappedWorldMap
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.staticView.SquareView
import agh.ics.gameoflife.statistics.CsvWriter
import agh.ics.gameoflife.statistics.Options
import agh.ics.gameoflife.statistics.Statistics
import agh.ics.gameoflife.view.GridCell
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.measureTimeMillis


fun runSimulation(running: MutableState<Boolean>, time: Long, engine: IEngine, statistics: Statistics) {
    var info: Long = 0
    val fileName = buildString {
        this.append("sim")
        this.append(if (engine is MagicEngine) "Magic" else "Plain")
        this.append(if (engine.map is WallWorldMap) "WallMap" else "WrappedMap")
        this.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd_HH-mm-ss")))
        this.append(".csv")
    }

    while (true) {
        if (time - info > 0) {
            sleep(time - info)
        } else {
            sleep(10)
        }
        info = measureTimeMillis {
            if (running.value) {
                engine.runIteration()
                statistics.recalculate()
            } else {
                if (statistics.dataList.isNotEmpty()) {
                    CsvWriter.flush(fileName, statistics.dataList)
                    statistics.dataList = mutableListOf()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun getGridView(engine: IEngine, opts: Options, squareView: Map<String, Painter>, running: MutableState<Boolean>) {
    Column(modifier = Modifier.size(((opts.height + 1) * 30).dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End)
    {
        val siz2 = opts.width + 1
        LazyVerticalGrid(cells = GridCells.Fixed(siz2)) {
            items(siz2 * (opts.height + 1)) {
                val position = Vector2d(it % siz2, it / siz2)
                val a = GridCell(
                    engine.map.getViewObj(position),
                    engine.map.statistics,
                    engine.map.returnBackGroundColor(position),
                    running
                )
                a.getView(squareView)
            }
        }
    }
}


@OptIn(ExperimentalGraphicsApi::class)
@Preview
@Composable
fun getMainView(running: MutableState<Boolean>, opts: Options, isWrapped: Boolean) {
    val statistics = Statistics()

    val map = if (isWrapped) {
        WrappedWorldMap(statistics, opts)
    } else {
        WallWorldMap(statistics, opts)
    }

    val engine = if (opts.isMagicEngine) {
        MagicEngine(map, 3, opts)
    } else {
        PlainEngine(map, opts)
    }

    engine.placeAnimals((((opts.width + 1) * (opts.height + 1)).toFloat() * 0.3).toInt())

    statistics.init()
    statistics.recalculate()

    CoroutineScope(Dispatchers.Default).launch {
        runSimulation(running, 30, engine, statistics)
    }

    val squareView = SquareView.views()

    MaterialTheme {
        Row(modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            getGridView(engine, opts, squareView, running)
            Column {
                Button(
                    onClick = {
                        running.value = !running.value
                    }) {
                    Text((if (running.value) "Stop" else "Start") + "simulation")
                }
                statistics.getView(engine)
            }
        }
    }
}

@Composable
fun run(opts: List<Options>) {
    lateinit var running1: MutableState<Boolean>
    runBlocking {
        CoroutineScope(Dispatchers.Default).launch {
            running1 = mutableStateOf(false)
        }.join()
    }

    lateinit var running2: MutableState<Boolean>
    runBlocking {
        CoroutineScope(Dispatchers.Default).launch {
            running2 = mutableStateOf(false)
        }.join()
    }

    val applicationState = remember { MyApplicationState(running1, running2) }

    if (applicationState.windows.size > 0) {
        for ((index, value) in applicationState.windows.withIndex()) {
            key(value) {
                MyWindow({ value.close() }, applicationState.runningList[index], opts[index], index % 2 == 0)
            }
        }
    }
}


@Composable
private fun MyWindow(state: () -> Unit, running: MutableState<Boolean>, opts: Options, isWrapped: Boolean) =
    Window(onCloseRequest = state, title = if (isWrapped) "Wrapped map" else "Walled map") {
        getMainView(running, opts, isWrapped)
    }

private class MyApplicationState(vararg running: MutableState<Boolean>) {
    val windows = mutableStateListOf<MyWindowState>()

    val runningList: List<MutableState<Boolean>>

    init {
        runningList = listOf(*running)
        repeat(this.runningList.size) {
            windows += MyWindowState { this.exit() }
        }
    }

    fun exit() {
        this.runningList.forEach { it.value = false }
        windows.clear()
    }
}

private class MyWindowState(
    val exit: () -> Unit,
) {

    fun close() = exit()
}
