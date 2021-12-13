package agh.ics.gameoflife.map

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.elements.Grass
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.regions.IRegion
import agh.ics.gameoflife.staticView.MutableWorldElement
import agh.ics.gameoflife.statistics.Options
import agh.ics.gameoflife.statistics.Statistics
import agh.ics.gameoflife.view.IGetView
import agh.ics.gameoflife.view.ITopElementObserver
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color

interface IWorldMap : IGetView, ITopElementObserver {
    var opts: Options
    var statistics: Statistics

    /**
     * Contains amount of [Animal]s currently living on map
     */
    var lenAnimals: Int

    /**
     * Translates vector when it can't go to desired [position]
     * Return [Pair] that says where the new position is [Vector2d] and whether it should even move [Boolean]
     */
    fun translateVector(position: Vector2d, oldPosition: Vector2d): Pair<Vector2d, Boolean>

    /**
     * Does the whole iteration on events, eating, breeding, etc
     */
    fun changeDay(currentIteration: Int)

    /**
     * Adds [animals] to map if their [Animal.position] match map boundaries
     */
    fun addAnimals(animals: List<Animal>)

    /**
     * Adds 2 grasses on random vector if possible
     * Returns how many grasses have been added
     */
    fun addGrass(): Int

    /**
     * Adds [value] randomly made Animals
     */
    fun addRandomAnimals(value: Int)

    /**
     * Gets the biggest [Animal] that is currently occupying [position]
     */
    fun getAnimal(position: Vector2d): Animal?

    /**
     * Gets [MutableState<MutableWorldElement>] that represents [position]
     */
    override fun getViewObj(position: Vector2d): MutableState<MutableWorldElement>

    /**
     * Returns [Color] based on [IRegion] that occupies [position]
     */
    fun returnBackGroundColor(position: Vector2d): Color

    /**
     * Gets 2 or fewer cells where [Grass] can be placed
     */
    fun getGrassesToPlace(): Pair<Vector2d?, Vector2d?>
    fun getGrassAmount(): Int

}
