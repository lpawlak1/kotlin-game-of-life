package agh.ics.gameoflife.engine

import agh.ics.gameoflife.map.IWorldMap
import kotlin.reflect.KClass

interface IEngine {
    val map: IWorldMap

    fun runIteration()
    fun placeAnimals(value: Int)
}