package agh.ics.gameoflife.map

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.statistics.Options
import agh.ics.gameoflife.statistics.Statistics

class WrappedWorldMap(
    statistics: Statistics,
    opts: Options
) : AbstractWorldMap(statistics, opts) {

    init {
        statistics.map = this
    }

    override fun translateVector(position: Vector2d, oldPosition: Vector2d): Pair<Vector2d, Boolean> {
        var (x, y) = position
        if (x > opts.width) {
            x = 0
        } else if (x < 0) {
            x = opts.width
        }
        if (y > opts.height) {
            y = 0
        } else if (y < 0) {
            y = opts.height
        }
        val moved = (oldPosition != Vector2d(x, y))
        return Pair(Vector2d(x, y), moved)
    }

    override fun addAnimals(animals: List<Animal>) {
        super.addAnimals(animals)
        animals.forEach { it.map = this }
    }

}
