package agh.ics.gameoflife.engine

import agh.ics.gameoflife.map.IWorldMap

interface IEngine {
    /**
     * You always need map to run simulation on it
     */
    val map: IWorldMap

    /**
     * Main method for starting all the necessary actions that have to take in one day
     */
    fun runIteration()

    /**
     * Places [howMany] (amount) randomly animals, with [initialLife] as their [agh.ics.gameoflife.elements.Animal.energy]
     */
    fun placeAnimals(howMany: Int, initialLife: Int)
}