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
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

const val moveEnergy = 4
const val eatValue = 50
const val breedMinValue = 20

internal fun HashMap<Vector2d, Square>.getEverytime(vec: Vector2d): Square {
    this.putIfAbsent(vec, Square(vec))
    return this[vec]!!
}

internal fun HashMap<Vector2d, Square>.removeNullSquares() {
    for (elem in this.filter { !it.value.isOccupied }) {
        this.remove(elem.key)
    }
}

abstract class AbstractWorldMap(animals: List<Animal>, private val jungle: Jungle, val width: Int, val height: Int) :
    IWorldMap {
    private val steppe: Stepee
    private val objectsMap: HashMap<Vector2d, Square> = hashMapOf()

    val mutableStates = Array(width + 1) { Array(height + 1) { mutableStateOf("") } }

    init {
        steppe = Stepee(Vector2d(0, 0), Vector2d(width, height), jungle.lowerLeft, jungle.upperRight)
        for (animal in animals) {
            this.objectsMap.getEverytime(animal.position).place(animal)
        }
    }

    override fun returnBackGroundColor(position: Vector2d): Color {
        return if (position in jungle) jungle.retColor()
        else steppe.retColor()
    }

    override fun getAnimal(position: Vector2d): Animal? {
        if (!objectsMap.containsKey(position)) {
            return null
        }
        return objectsMap[position]!!.biggestAnimal()
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
        addGrass(steppe)

        this.objectsMap.forEach {
            val (position, square) = it
            val (x, y) = position
            try {
                this.mutableStates[x][y].value =
                    square.toString() //TODO inverse this to observer coz its to lame and slow
            } catch (e: Exception) {
                println("essunia $square ${square.animals} $position")
            }
        }
        this.objectsMap.removeNullSquares() //Remove squares that don't need to be here
    }

    override fun addAnimals(animals: List<Animal>) {
        with(this.objectsMap) {
            animals.forEach { getEverytime(it.position).place(it) }
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

        this.mutableStates[grass.x][grass.y].value = this.objectsMap.getEverytime(Vector2d(grass.x, grass.y)).toString()
    }

    override fun getViewObj(position: Vector2d): MutableState<String> {
        return this.mutableStates[position.x][position.y]
    }
}


