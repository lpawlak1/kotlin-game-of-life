package agh.ics.gameoflife.model

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.elements.Grass
import agh.ics.gameoflife.position.Vector2d

class Square(val position: Vector2d) {
    /**
     * It is main container for all [Animal] in Square
     * Animals are sorted by their [Animal.energy] left, the smallest [Animal.energy] first
     */
    internal var animals: MutableList<Animal> = mutableListOf()

    /**
     * As only one [Grass] can be in Square just one mutable field is enough
     * It can be nullable
     */
    private var grass: Grass? = null

    /**
     * Before anything in simulation we have to add 1 to their [Animal.lifeSpan]
     * and [Animal.energy] should be deducted by 1
     */
    fun changeDay(moveEnergy: Int) {
        for (animal in animals) {
            animal.deduceDayLife(moveEnergy)
            animal.lifeSpan = animal.lifeSpan + 1
        }
    }

    /**
     * Removes every dead [Animal] from square
     */
    fun removeDead() {
        animals.removeIf { it.energy <= 0 }
    }

    /**
     * Rotates and moves every animal to desired spot, uses their genes to do so, updates last move index
     * @return List of animals that have moved, not all as some might be here because they already moved in this day
     */
    fun move(currentIteration: Int): List<Animal> {
        val (math, rest) = animals.partition { it.move(currentIteration) }
        this.animals = rest as MutableList<Animal>
        return math
    }

    /**
     * Performs eat action on biggest animal in square (if possible)
     * [eatValue] is value of amount of energy per grass eaten
     */
    fun eat(eatValue: Int) {
        if (grass != null && animals.size != 0) {
            val anim: Animal = this.biggestAnimal()!!
            val list: List<Animal> = this.animals.filter { it.energy == anim.energy }
            val dividedEnergy: Int = (eatValue / list.size)
            for (animal in list) {
                animal.eat(dividedEnergy)
            }
            this.grass = null
        }
    }

    /**
     * Breeds new animal from 2 biggest animals
     * @return true if animal was successfully breded
     */
    fun breed(min_life_required: Int): Boolean {
        if (animals.size >= 2) {
            val first: Animal = this.pollBiggestAnimal()
            val second: Animal = this.pollBiggestAnimal()

            if (second.energy < min_life_required || first.energy < min_life_required) {
                place(second)
                place(first)
                return false
            }

            val newAnimal: Animal = Animal.breed(first, second)
            place(second)
            place(first)
            place(newAnimal)
            return true
        }
        return false
    }

    /**
     * Puts animal into this square, caller should handle whether this animal can go here and should it be here
     * @param animal [Animal] to be put into square
     */
    fun place(animal: Animal): Boolean {
        animals.add(animal)
        return true
    }

    /**
     * Places grass into square if possible
     * @return whether grass was put into square (true - placed, false - not)
     */
    fun placeGrass(): Boolean {
        if (grass == null) {
            grass = Grass(this.position)
            return true
        }
        return false
    }

    /**
     * Get animal with the most life points
     * @return [Animal] or null if none animals are present
     */
    fun biggestAnimal(): Animal? {
        return animals.maxWithOrNull(compareBy { it.energy })
    }

    /**
     * Get animal with the most life points, and removes it
     * @return [Animal] or throws [Exception] if no animals in list
     */
    fun pollBiggestAnimal(): Animal {
        val ret: Animal = this.biggestAnimal()!!
        this.animals.removeIf { ret === it }
        return ret
    }

    override fun toString(): String {
        return (this.biggestAnimal() ?: this.grass ?: "").toString()
    }

    /**
     * Gets information whether this square is Occupied - are animals ([Animal]) in it or is grass ([Grass]) inside
     * @return True if anything is present, false otherwise
     */
    val isOccupied: Boolean
        get() = animals.size != 0 || grass != null


}
