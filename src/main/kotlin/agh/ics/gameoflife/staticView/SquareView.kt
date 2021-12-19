package agh.ics.gameoflife.staticView

import agh.ics.gameoflife.elements.AbstractElement
import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.elements.Grass
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource


class SquareView {
    companion object {
        /**
         * Is used only once so painterResource is not getting images more than once
         */
        @Composable
        fun views(): Map<String, Painter> {
            return mapOf(
                ViewValues.animal to painterResource("animal.png"),
                ViewValues.nul to painterResource("empty.png"),
                ViewValues.grass to painterResource("grass2.png")
            )
        }
    }
}

class ViewValues {
    companion object {
        const val animal = "animal"
        const val grass = "grass"
        const val nul = "null"
    }
}

data class MutableWorldElement(val classed: AbstractElement?, val value: Int) {
    fun getName(): String {
        if (classed == null) {
            return ViewValues.nul
        }
        return when (classed::class) {
            Animal::class -> ViewValues.animal
            Grass::class -> ViewValues.grass
            else -> ViewValues.nul
        }
    }

    companion object {
        val Default = MutableWorldElement(null, 0)
    }
}
