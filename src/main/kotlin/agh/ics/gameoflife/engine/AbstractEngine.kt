package agh.ics.gameoflife.engine

import agh.ics.gameoflife.map.IWorldMap
import kotlin.reflect.KClass

abstract class AbstractEngine(override val map: IWorldMap): IEngine {

    override fun runIteration() {
        map.changeDay(1)
    }

    override fun placeAnimals(value: Int) {
        this.map.addRandomAnimals(value, 40) //TODO opcje na zycie
    }
}