package agh.ics.gameoflife.map

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.statistics.Options
import agh.ics.gameoflife.statistics.Statistics

class WallWorldMap(
    val animals: List<Animal>,
    statistics: Statistics,
    opts: Options
) : AbstractWorldMap(animals, statistics, opts) {

    init {
        this.animals.forEach { it.map = this }
    }

    override fun translateVector(position: Vector2d, oldPosition: Vector2d): Pair<Vector2d, Boolean> {
        var (x, y) = position
        if (position.x > opts.width) {
            x = opts.width
        }
        if (position.y > opts.height) {
            y = opts.height
        }
        if (position.x < 0) {
            x = 0
        }
        if (position.y < 0) {
            y = 0
        }
        val moved = (oldPosition != Vector2d(x, y))
        return Pair(Vector2d(x, y), moved)
    }

    override fun addAnimals(animals: List<Animal>) {
        super.addAnimals(animals)
        animals.forEach { it.map = this }
    }
}
