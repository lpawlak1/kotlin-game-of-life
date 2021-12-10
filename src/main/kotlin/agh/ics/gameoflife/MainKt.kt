package agh.ics.gameoflife

import agh.ics.gameoflife.engine.IEngine
import agh.ics.gameoflife.engine.MagicEngine
import agh.ics.gameoflife.map.WrappedWorldMap
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.staticView.SquareView
import agh.ics.gameoflife.statistics.Options
import agh.ics.gameoflife.statistics.Statistics
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep


fun runSimulation(running: MutableState<Boolean>, time: Int, engine: IEngine, staty: Statistics) {
    while (true) {
        sleep(time.toLong())
        if (running.value) {
            engine.runIteration()
            staty.recalculate()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun getGridView(engine: IEngine, opts: Options, squareView: Map<String, Painter>) {

    Column(modifier = Modifier.size(((opts.height + 1) * 30).dp)) {
        val siz2 = opts.width + 1
        LazyVerticalGrid(cells = GridCells.Fixed(siz2)) {
            items(siz2 * (opts.height + 1)) {
                val x = it / siz2
                val y = it % siz2
                val a = GridCell(
                    engine.map.getViewObj(Vector2d(x, y)),
                    engine.map,
                    x,
                    y,
                    engine.map.returnBackGroundColor(Vector2d(x, y))
                )
                a.getView(squareView)
            }
        }
    }
}


@Preview
@Composable
fun getMainView(running: MutableState<Boolean>) {

    val opts = Options(10, 10, 10, 7, 1, 0.5)

    val staty = Statistics()
    val map = WrappedWorldMap(emptyList(), staty, opts)
    staty.map = map
    val engine = MagicEngine(map, 3, opts)
    engine.placeAnimals(10)

    staty.init()
    staty.recalculate()

    CoroutineScope(Dispatchers.Default).launch {
        runSimulation(running, 2000, engine, staty)
    }

    val squareView = SquareView().getAnimalViews()

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

                Text("Grass: " + staty.grassAmountMS.value.toString())
                Text("Animals: " + staty.animalsAmountMS.value.toString())
                Text("Avg of dead animal lifespan: " + staty.varDeadLifeSpanMS.value.toString())
                Text("Avg of living animal energy: " + staty.varLivingEnergyMS.value.toString())
                Text("Dead by today: " + staty.amountOfDeadAnimalsMS.value.toString())
                Text("Today: " + staty.numberOfDay.value.toString())
                if (staty.maxGenotypeMS.value != "") {
                    Text("Max Genotype: \n ${staty.maxGenotypeMS.value}")
                    Text("Max Genotype amount: ${staty.maxGenotypeAmountMS.value}")
                }
            }
            getGridView(engine, opts, squareView)
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
            onCloseRequest = { running.value = false;exitApplication() },
            title = "Simulation",
            state = rememberWindowState(
                position = WindowPosition(alignment = Alignment.Center),
            )
        ) {
            getMainView(running)
        }

    }
}