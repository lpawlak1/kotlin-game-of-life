package agh.ics.gameoflife.map

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.regions.Jungle

class WrappedWorldMap(
    val animals: List<Animal>,
    jungle_region: Jungle,
    width: Int,
    height: Int): AbstractorWorldMap(animals, jungle_region, width, height) {

    init{
        this.animals.forEach{it.map = this}
    }

    override fun translateVector(position: Vector2d): Pair<Vector2d, Boolean> {
        var (x,y) = position
        if (position.x > width){
            x = 0
        }
        if (position.y > height){
            y = 0
        }
        if (position.x < 0){
            x = width
        }
        if (position.y < 0){
            y = height
        }
        val moved = (position == Vector2d(x,y))
        return Pair(Vector2d(x,y), moved)
    }

    override fun addAnimals(animals: List<Animal>) {
        super.addAnimals(animals)
        animals.forEach{it.map = this}
    }

    override fun addRandomAnimals(value: Int, initialLife: Int) {
        super.addRandomAnimals(value, initialLife)
    }
}
