package agh.ics.gameoflife.staticView

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource


class SquareView {
    @Composable
    fun getAnimalViews(): Map<String, Painter> {
        return mapOf(
            ViewValues.animal to painterResource("animal.png"),
            ViewValues.nul to painterResource("empty.png"),
            ViewValues.grass to painterResource("grass2.png")
        )
    }
}

class ViewValues {
    companion object {
        const val animal = "animal"
        const val grass = "grass"
        const val nul = "null"
    }
}