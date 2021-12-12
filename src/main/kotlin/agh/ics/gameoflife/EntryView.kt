package agh.ics.gameoflife

import agh.ics.gameoflife.statistics.Options
import agh.ics.gameoflife.view.InputField
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlin.math.sqrt

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

    var options: Options? = null

    @OptIn(ExperimentalFoundationApi::class)
    @Preview
    @Composable
    fun getView(nextShown: MutableState<Boolean>, options: MutableState<Options>) {

        widthField.init2()
        heightField.init2()
        startEnergyField.init2()
        moveEnergyField.init2()
        plantEnergyField.init2()
        jungleRatioField.init2()

        MaterialTheme {
            var width = remember { mutableStateOf("10") }
            var height = remember { mutableStateOf("10") }
            var startEnergy = remember { mutableStateOf("50") }
            var moveEnergy = remember { mutableStateOf("3") }
            var plantEnergy = remember { mutableStateOf("100") }
            var jungleRatio = remember { mutableStateOf("0.5") }
            val checkedState = remember { mutableStateOf(true) }
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

                Row{
                    Checkbox(
                        checked = checkedState.value,
                        onCheckedChange = { checkedState.value = it }
                    )
                    Text("Czy silnik ma być magiczny?")
                }

                Button(onClick = {
                    if(widthField.noErrors.value && heightField.noErrors.value && startEnergyField.noErrors.value && moveEnergyField.noErrors.value && plantEnergyField.noErrors.value && jungleRatioField.noErrors.value){
                        nextShown.value = true
                        options.value = Options(width.value.toInt(), height.value.toInt(), startEnergy.value.toInt(), moveEnergy.value.toInt(), plantEnergy.value.toInt(), jungleRatio.value.toDouble(), checkedState.value)
                    }
                }){
                    if(widthField.noErrors.value && heightField.noErrors.value && startEnergyField.noErrors.value && moveEnergyField.noErrors.value && plantEnergyField.noErrors.value && jungleRatioField.noErrors.value){
                        Text("Dalej")
                    }
                    else{
                        Text("Plis popraw błędy")
                    }
                }

            }
        }
    }

}


fun main() =
    application {
        val entryView = EntryView()
        val nextShown = remember { mutableStateOf(false) }
        val opts = remember { mutableStateOf(Options(0,0,0,0,0,0.0, true)) }
        if (!nextShown.value) {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Input entry values",
                state = rememberWindowState(
                    position = WindowPosition(alignment = Alignment.Center),
                )
            ) {
                entryView.getView(nextShown, opts)
            }
        }
        else if (nextShown.value){
            val opts2 = opts.value.copy(width = opts.value.width-1, height = opts.value.height-1, jungleRatio = sqrt(opts.value.jungleRatio))
            siema.run(opts2)
        }

    }
