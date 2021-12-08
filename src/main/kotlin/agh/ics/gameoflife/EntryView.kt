package agh.ics.gameoflife

import agh.ics.gameoflife.view.InputField
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

class EntryView {
    val intRegex = Regex("^[1-9][0-9]*$")
    val junglRatioRegex = Regex("^0\\.[0-9]+$")

    val widthField =
        InputField(intRegex, "Width: ", { it.toInt() < 100 }, "Width has to be 0<x<100 and an Integer", null)
    val heightField =
        InputField(intRegex, "Height: ", { it.toInt() < 100 }, "Height has to be 0<x<100 and an Integer", null)
    val startEnergyField = InputField(intRegex, "Start energy: ", { true }, "Start energy has to be an Integer", null)
    val moveEnergyField = InputField(intRegex, "Move energy: ", { true }, "Move energy has to be an Integer", null)
    val plantEnergyField = InputField(intRegex, "Plant energy: ", { true }, "Plant energy has to be an Integer", null)
    val jungleRatioField = InputField(
        junglRatioRegex,
        "Jungle ratio: ",
        { it.toDouble() < 1.00 },
        "Jungle ratio has to be 0<x<1 and an Double with .",
        null
    )

    @OptIn(ExperimentalFoundationApi::class)
    @Preview
    @Composable
    fun getView() {
        MaterialTheme {
            var width = remember { mutableStateOf("") }
            var height = remember { mutableStateOf("") }
            var startEnergy = remember { mutableStateOf("") }
            var moveEnergy = remember { mutableStateOf("") }
            var plantEnergy = remember { mutableStateOf("") }
            var jungleRatio = remember { mutableStateOf("") }
            Column {
                widthField.value = width
                widthField.getView()

                heightField.value = height
                heightField.getView()

                startEnergyField.value = startEnergy
                startEnergyField.getView()

                moveEnergyField.value = moveEnergy
                moveEnergyField.getView()

                plantEnergyField.value = plantEnergy
                plantEnergyField.getView()

                jungleRatioField.value = jungleRatio
                jungleRatioField.getView()

            }
        }
    }

}


fun main() {
    val entryView = EntryView()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Input entry values",
            state = rememberWindowState(
                position = WindowPosition(alignment = Alignment.Center),
            )
        ) {
            entryView.getView()
        }

    }
}
