package agh.ics.gameoflife.map

import agh.ics.gameoflife.elements.AbstractElement
import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.model.Square
import agh.ics.gameoflife.position.MapDirection
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.regions.Jungle
import agh.ics.gameoflife.regions.Stepee
import agh.ics.gameoflife.staticView.MutableWorldElement
import agh.ics.gameoflife.statistics.Options
import agh.ics.gameoflife.statistics.Statistics
import agh.ics.gameoflife.view.ITopElementObserver
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import kotlin.random.Random


internal fun HashMap<Vector2d, Square>.getEverytime(vec: Vector2d, topElementObserver: ITopElementObserver): Square {
    this.putIfAbsent(vec, Square(vec))
    this[vec]!!.addObserver(topElementObserver)
    return this[vec]!!
}

internal fun HashMap<Vector2d, Square>.removeNullSquares() {
    for (elem in this.filter { !it.value.isOccupied }) {
        this.remove(elem.key)
    }
}


abstract class AbstractWorldMap(
    animals: List<Animal>,
    final override var statistics: Statistics,
    final override var opts: Options
) : IWorldMap {

    private val jungle: Jungle = Jungle.calculateJungle(opts.width, opts.height, opts.jungleRatio)

    private val steppe: Stepee =
        Stepee(Vector2d(0, 0), Vector2d(opts.width, opts.height), jungle.lowerLeft, jungle.upperRight)
    private val objectsMap: HashMap<Vector2d, Square> = hashMapOf()

    private val mutableStates =
        Array(opts.width + 1) { Array(opts.height + 1) { mutableStateOf(MutableWorldElement.Default) } }


    init {
        for (animal in animals) {
            this.objectsMap.getEverytime(animal.position, this).place(animal)
            this.statistics.animals.add(animal)
        }
    }

    override fun returnBackGroundColor(position: Vector2d): Color {
        return if (position in jungle) jungle.retColor()
        else steppe.retColor()
    }

    override fun notify(element: AbstractElement?, position: Vector2d) {
        val (x, y) = position
        if (element is Animal) {
            this.mutableStates[x][y].value = MutableWorldElement(element, element.energy)
        }
        this.mutableStates[x][y].value = MutableWorldElement(element, 0)
    }

    override fun getAnimal(position: Vector2d): Animal? {
        if (!objectsMap.containsKey(position)) {
            return null
        }
        return objectsMap[position]!!.biggestAnimal()
    }

    override var lenAnimals: Int = 0
        get() = this.statistics.animals.size

    override fun changeDay(currentIteration: Int) {
        val movedAnimals: MutableList<List<Animal>> = mutableListOf()
        for ((_, square) in objectsMap) {
            square.changeDay(this.opts.moveEnergy)
            val (sum, size) = square.removeDead()
            this.statistics.amountOfDeadAnimals += size
            this.statistics.sumOfDeadLifeSpan += sum
            val animals = square.move(currentIteration)
            movedAnimals.add(animals)
        }

        movedAnimals.forEach(this::addAnimals)

        this.statistics.animals = this.statistics.animals.filter { it.energy > 0 }.toMutableList()

        for ((_, square) in objectsMap) {
            val eaten = square.eat(this.opts.plantEnergy)
            if (eaten) this.statistics.grassAmount -= 1

            val animal = square.breed(this.opts.breedEnergy)
            if (animal != null) {
                this.statistics.animals.add(animal)
            }
        }

        this.objectsMap.removeNullSquares() //Remove squares that don't need to be here

        val addedAmount = addGrass()
        this.statistics.grassAmount += addedAmount
    }

    /**
     * Only when they moved not when appear for first time or resurrect
     */
    override fun addAnimals(animals: List<Animal>) {
        with(this.objectsMap) {
            animals.forEach { getEverytime(it.position, this@AbstractWorldMap).place(it) }
        }
    }

    override fun getGrassesToPlace(): Pair<Vector2d?, Vector2d?> {
        fun getRandomGrassPosition(vectorList: List<Vector2d>): Vector2d? =
            if (vectorList.isEmpty()) {
                null
            } else {
                vectorList[Random.nextInt(vectorList.size)]
            }

        val listJungle = mutableListOf<Vector2d>()
        val listSteppe = mutableListOf<Vector2d>()
        for (i in 0..this.opts.width) {
            for (j in 0..this.opts.height) {
                val vec = Vector2d(i, j)
                if (vec !in this.objectsMap) {
                    if (vec in this.jungle) {
                        listJungle.add(vec)
                    } else if (vec in this.steppe) {
                        listSteppe.add(vec)
                    }
                }
            }
        }
        return getRandomGrassPosition(listJungle) to getRandomGrassPosition(listSteppe)
    }

    override fun addRandomAnimals(value: Int) {
        fun MutableList<Animal>.isVectorIn(vec: Vector2d): Boolean {
            return this.any { it.position == vec }
        }

        val animals: MutableList<Animal> = mutableListOf()

        var i = 1
        do {
            val x = Random.nextInt(0, opts.width + 1)
            val y = Random.nextInt(0, opts.height + 1)

            if (!animals.isVectorIn(Vector2d(x, y))) {
                val animal = Animal(
                    Vector2d(x, y),
                    MapDirection.directionFactory(Random.nextInt(8)),
                    this.opts.startEnergy,
                    Animal.getRandomGenes()
                )
                animals.add(animal)

                this.statistics.animals.add(animal)

                i++
            }
        } while (i <= value)
        addAnimals(animals)
    }

    override fun addGrass(): Int {
        val (jungleVector, steppeVector) = this.getGrassesToPlace()

        var ret = 0

        if (jungleVector != null) {
            this.objectsMap.getEverytime(jungleVector, this).placeGrass()
            ret++
        }

        if (steppeVector != null) {
            this.objectsMap.getEverytime(steppeVector, this).placeGrass()
            ret++
        }

        return ret
    }


    override fun getViewObj(position: Vector2d): MutableState<MutableWorldElement> {
        return this.mutableStates[position.x][position.y]
    }


    override fun getGrassAmount(): Int {
        return this.objectsMap.values.filter { it.grass != null }.size
    }

}


