package agh.ics.gameoflife.view

import agh.ics.gameoflife.map.IWorldMap
import agh.ics.gameoflife.position.Vector2d
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource


class GridCell(
    private val text: MutableState<String>,
    val map: IWorldMap,
    val x: Int,
    val y: Int,
    val background: Color
) {

    @Preview
    @Composable
    fun getView(img: androidx.compose.ui.graphics.painter.Painter) {
        TooltipArea(tooltip = {
            Text(map.getViewObj(Vector2d(x, y)).value)
        }) {
            Surface(color = background) {
                if (text.value == "*") {
                    Image(painter = painterResource("grass2.png"), "", modifier = Modifier.fillMaxSize())
                } else if (text.value != "") {
                    Image(img, "", modifier = Modifier.fillMaxSize())
                } else {
                    Image(painter = painterResource("empty.png"), "", modifier = Modifier.fillMaxSize())
                }

            }
        }
    }
}