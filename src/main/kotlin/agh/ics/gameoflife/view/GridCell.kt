package agh.ics.gameoflife.view

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.map.IWorldMap
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.staticView.MutableWorldElement
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import kotlin.math.max
import kotlin.math.min


class GridCell(
    private val topObject: MutableState<MutableWorldElement>,
    val map: IWorldMap,
    val x: Int,
    val y: Int,
    val background: Color,
    val running: MutableState<Boolean>
) {

    @OptIn(ExperimentalGraphicsApi::class)
    @Composable
    fun getView(painters: Map<String, Painter>) {
        Surface(color = background) {
            if (topObject.value.classe is Animal){
                if (running.value) {
                    Image(
                        painter = painters[topObject.value.getName()]!!,
                        "",
                        colorFilter = ColorFilter.tint(
                            Color.hsl(
                                0.0F,
                                max(
                                    min(1.0F, ((topObject.value.classe as Animal).energy.toFloat() / 100.0F)),
                                    0.1F
                                ), 0.5F
                            )
                        ),
                        modifier = Modifier.fillMaxSize()
                            .graphicsLayer(rotationZ = 45.0F * (topObject.value.classe as Animal).direction.`val`)
                    )
                }
                else {
                    TooltipArea(tooltip = {
                        Surface( color = Color.hsl(164.0F,0.53F,0.5F,1.0F)) {
                            val animal = map.getAnimal(Vector2d(x,y))!!
                            Column {
                                Text(map.getViewObj(Vector2d(x, y)).value.getName())
                                Text("Energy: ${animal.energy}")
                                Text("Life span: ${animal.lifeSpan}")
                                Text("Direction: ${animal.direction}")
                                Text("Amount of children: ${animal.amountOfChilds}")
                                Text("Genes: ${animal.genes.contentDeepToString()}")
                                if (animal.isTracked){
                                    Text("Animal is tracked")
                                }
                                else{
                                    Button({
                                        println("tracking him now :)")
                                        map.statistics.trackedAnimal = animal
                                    }){
                                        Text("Track animal")
                                    }
                                }

                            }
                        }
                    }) {
                        Image(
                            painter = painters[topObject.value.getName()]!!,
                            "",
                            colorFilter = ColorFilter.tint(
                                Color.hsl(
                                    0.0F,
                                    max(
                                        min(1.0F, ((topObject.value.classe as Animal).energy.toFloat() / 100.0F)),
                                        0.1F
                                    ), 0.5F
                                )
                            ),
                            modifier = Modifier.fillMaxSize()
                                .graphicsLayer(rotationZ = 45.0F * (topObject.value.classe as Animal).direction.`val`)
                        )

                    }
                }
            }
            else{
                Image(painter = painters[topObject.value.getName()]!!, "", modifier = Modifier.fillMaxSize())
            }
        }
    }
}