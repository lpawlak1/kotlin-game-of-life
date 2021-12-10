package agh.ics.gameoflife.engine

import agh.ics.gameoflife.map.IWorldMap
import agh.ics.gameoflife.statistics.Options

open class PlainEngine(final override val map: IWorldMap, private val opts: Options) : IEngine {

    init {
        map.opts = opts
    }

    override fun runIteration() {
        map.changeDay(1)
    }

    override fun placeAnimals(howMany: Int) {
        this.map.addRandomAnimals(howMany)
    }
}