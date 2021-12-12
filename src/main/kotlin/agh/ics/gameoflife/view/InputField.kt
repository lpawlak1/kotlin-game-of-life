package agh.ics.gameoflife.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

class InputField(
    val regex: Regex,
    val name: String,
    val check: (value: String) -> Boolean,
    val errorMessage: String,
    var value: MutableState<String>?
) {
    lateinit var noErrors: MutableState<Boolean>

    @Composable
    fun init2(){
        noErrors = remember { mutableStateOf(true) }
    }

    @Composable
    fun getView() {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, textAlign = TextAlign.Left)
                TextField(value!!.value, {
                    value!!.value = it
                })
            }
            if (!regex.matches(value!!.value) || !check(value!!.value)) {
                Text(errorMessage, style = TextStyle(Color.Red))
                noErrors.value = false
            } else {
                noErrors.value = true
            }
        }

    }
}
