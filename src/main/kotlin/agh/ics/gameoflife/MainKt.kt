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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


fun runSimulation(running: MutableState<Boolean>, time: Long, engine: IEngine, staty: Statistics) {
    var info: Long = 0
    val fileName = buildString {
        this.append("sim")
        if (engine is MagicEngine) {
            this.append("Magic")
        } else {
            this.append("Plain")
        }
        if (engine.map is WallWorldMap) {
            this.append("WallMap")
        } else {
            this.append("WrappedMap")
        }
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
                staty.recalculate()
            } else {
                if (staty.dataList.isNotEmpty()) {
                    CsvWriter.flush(fileName, staty.dataList)
                    staty.dataList = mutableListOf()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun getGridView(engine: IEngine, opts: Options, squareView: Map<String, Painter>, running: MutableState<Boolean>) {

    Column(modifier = Modifier.size(((opts.height + 1) * 30).dp)) {
        val siz2 = opts.width + 1
        LazyVerticalGrid(cells = GridCells.Fixed(siz2)) {
            items(siz2 * (opts.height + 1)) {
                val x = it % siz2
                val y = it / siz2
                val a = GridCell(
                    engine.map.getViewObj(Vector2d(x, y)),
                    engine.map.statistics,
                    engine.map.returnBackGroundColor(Vector2d(x, y)),
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
    val staty = Statistics()
    val map = if (isWrapped) {
        WrappedWorldMap(emptyList(), staty, opts)
    } else {
        WallWorldMap(emptyList(), staty, opts)
    }
    staty.map = map

    val engine = if (opts.isMagicEngine) {
        MagicEngine(map, 3, opts)
    } else {
        PlainEngine(map, opts)
    }
    engine.placeAnimals((((opts.width + 1) * (opts.height + 1)).toFloat() * 0.3).toInt())

    staty.init()
    staty.recalculate()

    CoroutineScope(Dispatchers.Default).launch {
        runSimulation(running, 30, engine, staty)
    }

    val squareView = SquareView().getAnimalViews()

    MaterialTheme {
        Row(modifier = Modifier.padding(20.dp)) {
            Column {
                Button(
                    onClick = {
                        running.value = !running.value
                    }) {

                    val start = "Start"
                    val stop = "Stop"
                    Text("${if (running.value) stop else start} simulation")
                }

                Text("Grass: " + staty.grassAmountMS.value.toString())
                Text("Animals: " + staty.animalsAmountMS.value.toString())
                Text("Avg of dead animal lifespan: " + staty.varDeadLifeSpanMS.value.toString())
                Text("Avg of living animals' energy: " + staty.varLivingEnergyMS.value.toString())
                Text("Avg of living animals' children: " + staty.avgNumberOfChildsMS.value.toString())
                Text("Dead by today: " + staty.amountOfDeadAnimalsMS.value.toString())
                Text("Today: " + staty.numberOfDay.value.toString())
                if (staty.maxGenotypeMS.value != "") {
                    Text("Max Genotype: \n ${staty.maxGenotypeMS.value}")
                    Text("Max Genotype amount: ${staty.maxGenotypeAmountMS.value}")
                }
                if (engine is MagicEngine) {
                    if (engine.rescueTimes != engine.counter)
                        Text("Pozostało: ${engine.rescueTimes - engine.counter} ratunków")
                    else
                        Text("Wykorzystano wszystkie ratunki")
                }
                if (staty.trackedAnimal != null) {
                    Surface(color = Color.hsl(250.0F, 0.37F, 0.5F, 1.0F)) {
                        Column {
                            Text("Tracked animal statistics")
                            Text("Children amount ${staty.trackedAnimalChildrenMS.value}")
                            Text("Ancestors amount ${staty.trackedAnimalAncestorsMS.value}")
                            if (staty.trackedAnimal!!.energy <= 0) {
                                Text("Death date: ${staty.trackedAnimalDeathDateMS.value}")
                                Text("Lifespan: ${staty.trackedAnimalLifeSpanMS.value}")
                            }
                        }
                    }
                }
            }
            getGridView(engine, opts, squareView, running)
        }
    }
}

class siema {
    companion object {
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

            if (applicationState.windows.size == 2) {
                key(applicationState.windows[0]) {
                    MyWindow(applicationState.windows[0], applicationState.next(), opts[0], true)
                }

                key(applicationState.windows[1]) {
                    MyWindow(applicationState.windows[1], applicationState.next(), opts[1], false)
                }
            }
        }
    }
}


@Composable
private fun MyWindow(state: MyWindowState, running: MutableState<Boolean>, opts: Options, isWrapped: Boolean) =
    Window(onCloseRequest = { state.close() }, title = if (isWrapped) "Wrapped map" else "Walled map") {
        getMainView(running, opts, isWrapped)
    }

private class MyApplicationState(vararg running: MutableState<Boolean>) {
    val windows = mutableStateListOf<MyWindowState>()

    val runningList: List<MutableState<Boolean>>
    var next = false

    init {
        runningList = listOf(*running)
        windows += MyWindowState("Initial window", this, this.runningList[0])
        windows += MyWindowState("Second window", this, this.runningList[1])
    }

    fun next(): MutableState<Boolean> {
        return if (!next) {
            next = true
            this.runningList[0]
        } else {
            this.runningList[1]
        }
    }

    fun exit() {
        this.runningList.forEach { it.value = false }
        windows.clear()
    }

    private fun MyWindowState(
        title: String,
        obj: MyApplicationState,
        running: MutableState<Boolean>
    ) = MyWindowState(
        title,
        exit = obj::exit,
        windows::clear,
        running
    )
}

private class MyWindowState(
    val title: String,
    val exit: () -> Unit,
    private val close: () -> Unit,
    val running: MutableState<Boolean>
) {

    fun close() = exit()
}
