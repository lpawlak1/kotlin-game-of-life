package agh.ics.gameoflife.engine

import agh.ics.gameoflife.map.IWorldMap
import agh.ics.gameoflife.statistics.Options

/**
 * Extension of [PlainEngine], can rescue simulation by adding random Animals
 */
class MagicEngine(
    map: IWorldMap,
    /**
     * How many times simulation should be rescued by adding 5 [agh.ics.gameoflife.elements.Animal]
     */
    private val rescueTimes: Int,
    opts: Options
) : PlainEngine(map, opts) {

    private var counter: Int = 0

    override fun runIteration() {
        super.runIteration()
        if (map.lenAnimals < 5 && counter < rescueTimes) {
            this.placeAnimals(5)
            counter += 1
        }
    }
}