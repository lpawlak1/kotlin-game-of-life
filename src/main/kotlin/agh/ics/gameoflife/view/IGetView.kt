package agh.ics.gameoflife.view

import agh.ics.gameoflife.position.Vector2d
import androidx.compose.runtime.MutableState


interface IGetView {

    fun getViewObj(position: Vector2d): MutableState<String>
}
