package agh.ics.gameoflife.view

import agh.ics.gameoflife.map.IWorldMap
import agh.ics.gameoflife.position.Vector2d
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.size
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


class GridCell(private val text: MutableState<String>, val map: IWorldMap, val x: Int, val y: Int, val background: Color) {

    @Preview
    @Composable
    fun getView() {
        TooltipArea(tooltip = {
            Text(map.getViewObj(Vector2d(x, y)).value)
        }) {
            Surface(color = background) {
                Canvas(modifier = Modifier.size(10.dp, 10.dp)) {
                    if (text.value == "*") {
                        drawCircle(color = Color.Green)
                    } else if (text.value != "") {
                        drawCircle(color = Color.Red)
                    }
                }
            }
        }
    }
}