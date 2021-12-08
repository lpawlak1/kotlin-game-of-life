package agh.ics.gameoflife.engine

import agh.ics.gameoflife.map.IWorldMap

abstract class PlainEngine(override val map: IWorldMap) : IEngine {

    override fun runIteration() {
        map.changeDay(1) // TODO actually change iteration, needed for statistics
    }

    override fun placeAnimals(howMany: Int, initialLife: Int) {
        this.map.addRandomAnimals(howMany, initialLife) //TODO opcje na zycie
    }
}