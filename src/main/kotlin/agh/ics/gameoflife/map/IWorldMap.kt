package agh.ics.gameoflife.map

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.regions.IRegion
import agh.ics.gameoflife.view.IGetView
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

interface IWorldMap : IGetView {
    /**
     * Contains amount of [Animal]s currently living on map
     */
    var lenAnimals: Int

    /**
     * Translates vector when it can't go to desired [position]
     * Return [Pair] that says where the new position is [Vector2d] and whether it should even move [Boolean]
     */
    fun translateVector(position: Vector2d): Pair<Vector2d, Boolean>

    /**
     * Does the whole iteration on events, eating, breeding, etc
     */
    fun changeDay(currentIteration: Int)

    /**
     * Adds [animals] to map if their [Animal.position] match map boundaries
     */
    fun addAnimals(animals: List<Animal>)

    /**
     * Adds grass on random vector in specified region
     */
    fun addGrass(region: IRegion)

    /**
     * Adds [value] animals with their [Animal.energy] equal to [initialLife]
     */
    fun addRandomAnimals(value: Int, initialLife: Int)


    /**
     * Gets the biggest [Animal] that is currently occupying [position]
     */
    fun getAnimal(position: Vector2d): Animal?

    /**
     * Gets [MutableState<String>] that represents [position]
     */
    override fun getViewObj(position: Vector2d): MutableState<String>

    /**
     * Returns [Color] based on [IRegion] that occupies [position]
     */
    fun returnBackGroundColor(position: Vector2d): Color

}
