package agh.ics.gameoflife

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.engine.IEngine
import agh.ics.gameoflife.model.Square
import agh.ics.gameoflife.position.MapDirection
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.view.GridCell
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.*
import agh.ics.gameoflife.engine.*
import agh.ics.gameoflife.map.WallWorldMap
import agh.ics.gameoflife.map.WrappedWorldMap
import agh.ics.gameoflife.regions.Jungle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.ui.graphics.Color
import kotlin.system.measureTimeMillis

fun prepareEngine(): IEngine{
    val engine = MagicEngine(
        WrappedWorldMap(emptyList(), Jungle(Vector2d(30,30), Vector2d(70,70)), 100, 100)
    )
    engine.placeAnimals(1000)
    return engine
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun essa2(){
    val count = remember { mutableStateOf(10) }
    val engine = prepareEngine()

    MaterialTheme {
        LazyVerticalGrid(cells= GridCells.Fixed(100)){
            items(100*100){
                val x = it/100;
                val y = it%100;
                val a: GridCell = GridCell(engine.map.getViewObj(Vector2d(x,y)))
                a.getView()
            }
        }
        Button(
            onClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    engine.runIteration()
                }
            }) {

            Text("Run iteration")
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