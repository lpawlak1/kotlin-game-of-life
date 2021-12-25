package agh.ics.gameoflife.engine

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.map.AbstractWorldMap
import agh.ics.gameoflife.map.IWorldMap
import agh.ics.gameoflife.statistics.Options
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * Extension of [PlainEngine], can rescue simulation by adding random Animals
 */
class MagicEngine(
    map: IWorldMap,
    /**
     * How many times simulation should be rescued by adding 5 [agh.ics.gameoflife.elements.Animal]
     */
    val rescueTimes: Int,
    opts: Options
) : PlainEngine(map, opts) {

    var counter: MutableState<Int> = mutableStateOf(0)

    override fun runIteration() {
        super.runIteration()
        if (map.lenAnimals == 5 && counter.value < rescueTimes) {
            val toAdd = mutableListOf<Animal>()
            if (this.map is AbstractWorldMap) {
                this.map.statistics.animals.forEach {
                    toAdd += it.copy()
                }
            }
            this.map.addAnimals(toAdd)
            counter.value += 1
        }
    }
}