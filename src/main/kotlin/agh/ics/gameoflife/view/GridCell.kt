package agh.ics.gameoflife.view

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.staticView.MutableWorldElement
import agh.ics.gameoflife.statistics.Statistics
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ExperimentalGraphicsApi
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min


class GridCell(
    private val topObject: MutableState<MutableWorldElement>,
    val statistics: Statistics,
    private val background: Color,
    private val running: MutableState<Boolean>
) {

    @OptIn(ExperimentalGraphicsApi::class)
    @Composable
    fun getView(painters: Map<String, Painter>) {
        Surface(color = background) {
            if (topObject.value.classed is Animal) {
                if (running.value) {
                    getAnimalImage(painters[topObject.value.getName()]!!, topObject.value.classed as Animal)
                } else {
                    TooltipArea(tooltip = {
                        Surface(
                            color = MaterialTheme.colors.secondaryVariant,
                            shape = RoundedCornerShape(3),
                            modifier = Modifier.padding(20.dp)
                        ) {
                            val animal = topObject.value.classed as Animal
                            Column {
                                Text("Energy: ${animal.energy}")
                                Text("Life span: ${animal.lifeSpan}")
                                Text("Direction: ${animal.direction}")
                                Text("Amount of children: ${animal.amountOfChildren}")
                                Text("Genes: ${animal.genes.contentDeepToString()}")
                                if (animal.isTracked) {
                                    Text("Animal is tracked")
                                } else {
                                    val tracking = remember { mutableStateOf(false) }
                                    if (!tracking.value) {
                                        Button({
                                            statistics.trackedAnimal = animal
                                            tracking.value = true
                                        }) {
                                            Text("Track animal")
                                        }
                                    } else {
                                        Text("Animal is tracked")
                                    }
                                }
                            }
                        }
                    }) {
                        getAnimalImage(painters[topObject.value.getName()]!!, topObject.value.classed as Animal)
                    }
                }
            } else {
                Image(painter = painters[topObject.value.getName()]!!, "", modifier = Modifier.fillMaxSize())
            }
        }
    }


    @OptIn(ExperimentalGraphicsApi::class)
    @Composable
    private fun getAnimalImage(painter: Painter, animal: Animal) {
        Image(
            painter = painter,
            "",
            colorFilter = ColorFilter.tint(
                Color.hsl(
                    0.0F,
                    max(
                        min(1.0F, (animal.energy.toFloat() / 100.0F)),
                        0.1F
                    ), 0.5F
                )
            ),
            modifier = Modifier.fillMaxSize()
                .graphicsLayer(rotationZ = 45.0F * animal.direction.`val`)
        )
    }
}