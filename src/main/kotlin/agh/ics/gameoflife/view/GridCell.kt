package agh.ics.gameoflife.view

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.model.Square
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GridCell(val text : MutableState<String>) {

    @Preview
    @Composable
    fun getView() {
        Text("${text.value}")
    }
}