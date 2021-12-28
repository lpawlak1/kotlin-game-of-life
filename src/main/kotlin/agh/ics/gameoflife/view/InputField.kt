package agh.ics.gameoflife.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.lang.Exception

class InputField(
    private val regex: Regex,
    private val name: String,
    val check: (value: String) -> Boolean,
    private val errorMessageEnding: String,
    private val default: Any
) {
    lateinit var noErrors: MutableState<Boolean>
    lateinit var value: MutableState<String>

    private val errorMessage = buildString {
        this.append(name)
        this.append(" has to be ")
        this.append(errorMessageEnding)
    }

    @Composable
    fun init() {
        noErrors = remember { mutableStateOf(true) }
        value = remember { mutableStateOf("$default") }
    }

    private fun check(): Boolean{
        var ret = false
        try {
            ret = check(value.value)
        }catch (_: Exception){ }
        return ret
    }

    @Composable
    fun getView() {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(name, textAlign = TextAlign.Left, modifier = Modifier.width(200.dp))
                TextField(value.value, {
                    value.value = it
                }, modifier = Modifier.width(100.dp))
            }
            if (!regex.matches(value.value) || !check()) {
                Text(errorMessage, style = TextStyle(MaterialTheme.colors.error))
                noErrors.value = false
            } else {
                Text(" ")
                noErrors.value = true
            }
        }

    }
}
