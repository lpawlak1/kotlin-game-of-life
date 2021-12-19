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
    private val intRegex = Regex("^[1-9][0-9]*$")
    private val jungleRatioRegex = Regex("^0\\.[0-9]+$")

    var options: Options = Options.Default

    private val inputMap = hashMapOf(
        "width" to InputField(intRegex, "Width: ", { it.toInt() < 100 }, "be 0<x<100 and an Integer", options.width),
        "height" to InputField(intRegex, "Height: ", { it.toInt() < 100 }, "0<x<100 and an Integer", options.height),
        "startEnergy" to InputField(intRegex, "Start energy: ", { true }, "Integer", options.startEnergy),
        "moveEnergy" to InputField(intRegex, "Move energy: ", { true }, "Integer", options.moveEnergy),
        "plantEnergy" to InputField(intRegex, "Plant energy: ", { true }, "an Integer", options.plantEnergy),
        "jungleRatio" to InputField(jungleRatioRegex, "Jungle ratio: ", { it.toDouble() < 1.00 }, "0<x<1 and an Double with .", options.jungleRatio)
    )

    @OptIn(ExperimentalFoundationApi::class)
    @Preview
    @Composable
    fun getView(nextShown: MutableState<Boolean>, options: List<MutableState<Options>>) {

        inputMap.values.forEach { it.init() }

        lateinit var checkedState: List<MutableState<Boolean>>

        MaterialTheme {
            with(Options.Default) {
                checkedState = listOf(
                    remember { mutableStateOf(this.isMagicEngine) },
                    remember { mutableStateOf(this.isMagicEngine) }
                )
            }
            Column {

                for (inputField in inputMap.values) {
                    inputField.getView()
                }

                for ((index, checkedStateValue) in checkedState.withIndex()) {
                    Row {
                        Checkbox(
                            checked = checkedStateValue.value,
                            onCheckedChange = { checkedStateValue.value = it }
                        )
                        Text("Czy silnik mapy nr ${index + 1} ma być magiczny?")
                    }
                }

                Button(onClick = {
                    if (inputMap.values.all { it.noErrors.value }) {
                        for ((index, option) in options.withIndex()) {
                            option.value = Options(
                                width = inputMap["width"]!!.value.value.toInt(),
                                height = inputMap["height"]!!.value.value.toInt(),
                                startEnergy = inputMap["startEnergy"]!!.value.value.toInt(),
                                moveEnergy = inputMap["moveEnergy"]!!.value.value.toInt(),
                                plantEnergy = inputMap["plantEnergy"]!!.value.value.toInt(),
                                jungleRatio = inputMap["jungleRatio"]!!.value.value.toDouble(),
                                checkedState[index].value
                            )
                        }
                        nextShown.value = true
                    }
                }) {
                    if (inputMap.values.all { it.noErrors.value }) {
                        Text("Dalej")
                    } else {
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
        val optsList = listOf(
            remember { mutableStateOf(Options.Default) },
            remember { mutableStateOf(Options.Default) }
        )

        if (!nextShown.value) {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Enter entry values",
                state = rememberWindowState(
                    position = WindowPosition(alignment = Alignment.Center),
                )
            ) {
                entryView.getView(nextShown, optsList)
            }
        } else if (nextShown.value) {
            val optsCopyList = optsList.map {
                it.value.copy(
                    width = it.value.width - 1,
                    height = it.value.height - 1,
                    jungleRatio = sqrt(it.value.jungleRatio)
                )
            }
            run(optsCopyList)
        }

    }
