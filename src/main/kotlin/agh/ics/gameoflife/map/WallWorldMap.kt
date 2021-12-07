package agh.ics.gameoflife.map

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.*
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.regions.Jungle

class WallWorldMap(val animals: List<Animal>,jungle_region: Jungle, width: Int, height: Int): AbstractorWorldMap(animals, jungle_region, width, height) {

    init{
        this.animals.forEach{it.map = this}
    }

    override fun translateVector(position: Vector2d): Pair<Vector2d, Boolean> {
        var (x,y) = position
        if (position.x > width){
            x = width
        }
        if (position.y > height){
            y = height
        }
        if (position.x < 0){
            x = 0
        }
        if (position.y < 0){
            y = 0
        }
        val moved = (position != Vector2d(x,y))
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
