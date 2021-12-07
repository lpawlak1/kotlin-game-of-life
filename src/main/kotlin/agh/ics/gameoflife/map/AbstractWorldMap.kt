package agh.ics.gameoflife.map

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.model.Square
import agh.ics.gameoflife.position.MapDirection
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.regions.IRegion
import agh.ics.gameoflife.regions.Jungle
import agh.ics.gameoflife.regions.Stepee
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlin.random.Random

val moveEnergy = 4
val eatValue = 10
val breedMinValue = 20

internal fun HashMap<Vector2d, Square>.getEverytime(vec: Vector2d): Square {
    this.putIfAbsent(vec, Square(vec))
    return this[vec]!!
}

internal fun HashMap<Vector2d, Square>.removeNullSquares() {
    for (elem in this.filter { !it.value.isOcuppied }) {
        this.remove(elem.key)
    }
}

abstract class AbstractorWorldMap(animals: List<Animal>, jungle: Jungle, val width: Int, val height: Int) : IWorldMap {
    private val jungle: Jungle = jungle
    private val stepee: Stepee
    private val objectsMap: HashMap<Vector2d, Square> = hashMapOf()

    val mutableStates = Array(width) { Array(height) { mutableStateOf<String>("") } }

    init {
        stepee = Stepee(Vector2d(0, 0), Vector2d(width, height), jungle.lowerLeft, jungle.upperRight)
        for (animal in animals) {
            this.objectsMap.getEverytime(animal.position).place(animal)
        }
    }

    override fun changeDay(currentIteration: Int) {
        val movedAnimals: MutableList<List<Animal>> = mutableListOf()
        for ((_, square) in objectsMap) {
            square.changeDay(moveEnergy)
            square.removeDead()
            val animals = square.move(currentIteration)
            movedAnimals.add(animals)
        }

        movedAnimals.forEach(this::addAnimals)

        for ((_, square) in objectsMap) {
            square.eat(eatValue)
            square.breed(breedMinValue)
        }

        addGrass(jungle)
        addGrass(stepee)

        this.objectsMap.forEach {
            var (position,square) = it
            this.mutableStates[square.position.x][square.position.y].value = square.toString() //TODO iverse this to observer coz its to lame and slow
        }
        this.objectsMap.removeNullSquares() //Remove squares that doesn't need to be here
    }

    override fun addAnimals(animals: List<Animal>) {
        for (animal in animals) {
            this.objectsMap.getEverytime(animal.position).place(animal)
        }
    }

    override fun addRandomAnimals(value: Int, initialLife: Int) {
        val animals: MutableList<Animal> = mutableListOf()
        for (i in 1..value) {
            val x = Random.nextInt(0, width)
            val y = Random.nextInt(0, height)

            animals.add(
                Animal(
                    Vector2d(x, y),
                    MapDirection.directionFactory(Random.nextInt(8)),
                    initialLife,
                    Animal.getRandomGenes()
                )
            )

        }
        addAnimals(animals)
    }

    override fun addGrass(region: IRegion) {
        lateinit var grass: Vector2d
        do {
            grass = region.getRandomVector()
        } while (grass in this.objectsMap && !this.objectsMap[grass]!!.placeGrass())
        this.objectsMap.getEverytime(grass).placeGrass()

        this.mutableStates[grass.x][grass.y].value = this.objectsMap.getEverytime(Vector2d(grass.x,grass.y)).toString()
    }

    override fun getViewObj(position: Vector2d): MutableState<String> {
        return this.mutableStates[position.x][position.y]
//        return ((this.objectsMap.get(position)?.biggestAnimal() ?: this.objectsMap.get(position)?.grass
//        ?: "").toString())
    }
}


