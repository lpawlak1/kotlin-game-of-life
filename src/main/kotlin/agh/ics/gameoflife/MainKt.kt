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
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


fun runSimulation(running: MutableState<Boolean>, time: MutableState<Int>, engine: IEngine, statistics: Statistics) {
    val fileName = buildString {
        this.append("sim")
        this.append(if (engine is MagicEngine) "Magic" else "Plain")
        this.append(if (engine.map is WallWorldMap) "WallMap" else "WrappedMap")
        this.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd_HH-mm-ss")))
        this.append(".csv")
    }

    sleep(300)

    while (true) {
        sleep(time.value.toLong())

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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun getGridView(engine: IEngine, opts: Options, squareView: Map<String, Painter>, running: MutableState<Boolean>) {
    val height = opts.height+1
    val width = opts.width+1
    val maxWidth = 350.0f
    Column(
        modifier = Modifier.size(maxWidth.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    )
    {
        LazyVerticalGrid(cells = GridCells.Fixed(width)) {
            items(width*height) {
                val position = Vector2d(it % width, it / width)
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
fun getMainView(running: MutableState<Boolean>, opts: Options, isWrapped: Boolean, sleepTime: MutableState<Int>) {
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
        runSimulation(running, sleepTime, engine, statistics)
    }

    val squareView = SquareView.views()

    MaterialTheme {
        Column(modifier = Modifier.padding(3.dp)) {
            Row(
                modifier = Modifier.height(350.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Column {
                    getGridView(engine, opts, squareView, running)
                }
                Column {
                    Button(
                        onClick = {
                            running.value = !running.value
                        }) {
                        Text((if (running.value) "Stop" else "Start") + " simulation")
                    }
                    statistics.getTableView()
                }
            }
            statistics.getStaticView(engine)
        }
    }
}

@Preview
@Composable
fun timeSlider(timeValue: MutableState<Int>){
    val sliderPosition = remember { mutableStateOf(0.5f) }
    Column(modifier = Modifier.height(40.dp), horizontalAlignment = Alignment.CenterHorizontally){
        Text("${timeValue.value}ms delay")
        Slider(sliderPosition.value,{
            timeValue.value = calculateTime(it)
            sliderPosition.value = it
        })
    }
}

fun calculateTime(input: Float): Int{
    val outputStart = 16.0f
    val outputEnd = 500.0F
    val (inputStart, inputEnd) = 0.0f to 1.0f

    val output = outputStart + ((outputEnd - outputStart) / (inputEnd - inputStart)) * (input - inputStart)
    return output.toInt()
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
    Window(onCloseRequest = state,
        title = if (isWrapped) "Wrapped map" else "Walled map",
        state = rememberWindowState(
            position = WindowPosition(alignment = if (isWrapped) Alignment.TopStart else Alignment.TopEnd),
            height = 600.dp,
            width = 800.dp
        )
    ) {
        Column {
            val sleepTime = remember{mutableStateOf(calculateTime(0.5f))}
            getMainView(running, opts, isWrapped, sleepTime)
            timeSlider(sleepTime)
        }
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
