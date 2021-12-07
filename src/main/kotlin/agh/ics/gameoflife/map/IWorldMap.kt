package agh.ics.gameoflife.map

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.regions.IRegion
import agh.ics.gameoflife.view.IGetView
import androidx.compose.runtime.MutableState

interface IWorldMap : IGetView{
    fun translateVector(vector2d: Vector2d): Pair<Vector2d,Boolean>
    fun changeDay(currentIteration: Int)
    fun addAnimals(animals: List<Animal>)

    fun addGrass(region: IRegion)
    fun addRandomAnimals(value: Int, initialLife: Int)
    override fun getViewObj (position: Vector2d): MutableState<String>

}
