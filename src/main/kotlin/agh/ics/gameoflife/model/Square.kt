package agh.ics.gameoflife.model

import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.elements.Grass
import kotlin.Comparator

class Square(val position: Vector2d){
    /**
     * It is main container for all [Animal] in Square
     * Animals are sorted by their [Animal.life] left, the smallest [Animal.life] first
     */
    internal val animals : MutableList<Animal> = mutableListOf()

    /**
     * As only one [Grass] can be in Square just one mutable field is enough
     * It can be nullable
     */
    private var grass: Grass? = null

    /**
     * Before anything in simulation we have to add 1 to their [Animal.lifeSpan]
     * and [Animal.life] should be deducted by 1
     */
    fun changeDay(moveEnergy: Int) {
        for (animal in animals) {
            animal.deduceDayLife(moveEnergy)
//            animal.life = animal.life - 1
            animal.lifeSpan = animal.lifeSpan + 1
        }
    }

    /**
     * Removes every dead [Animal] from square
     */
    fun removeDead() {
        animals.removeIf { it.life <= 0 }
    }

    /**
     * Rotates and moves every animal to desired spot, uses their genes to do so, updates last move index
     * @return List of animals that have moved, not all as some might be here because they already moved in this day
     */
    fun move(currentIteration: Int): List<Animal> {
        val list = animals.filter { it.lastMove != currentIteration }
        animals.removeIf { it.lastMove != currentIteration }
        for(animal in list){
            animal.move(currentIteration)
        }
        return list
    }

    /**
     * Performs eat action on biggest animal in square (if possible)
     * [eatValue] is value of amount of energy per grass eaten
     */
    fun eat(eatValue: Int) {
        if (grass != null && animals.size != 0) {
            val anim: Animal = this.biggestAnimal()!!
            val list: List<Animal> = this.animals.filter{ it.life == anim.life }
            val dividedEnergy: Int = eatValue / list.size
            for(animal in list){
                animal.eat(dividedEnergy)
            }
            this.grass = null
        }
    }

    /**
     * Breeds new animal from 2 biggest animals
     * caller should handle the positioning of return value
     * @return newborn
     */
    fun breed(): Animal? {
        if (animals.size >= 2) {
            val first: Animal = this.pollBiggestAnimal()!!
            val second: Animal = this.pollBiggestAnimal()!!

            val newAnimal: Animal = Animal.breed(first,second)
            place(second)
            place(first)
            return newAnimal
        }
        return null
    }

    /**
     * Puts animal into this square, caller should handle whether this animal can go here and should it be here
     * @param animal [Animal] to be put into square
     */
    fun place(animal: Animal) : Boolean {
//        if (animals.size == 0 || animals.last()!!.life >= animal.life)
//            return false
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
        return animals.last()
    }

    /**
     * Get animal with the most life points, and removes it
     * @return [Animal] or null if none animals are present
     */
    fun pollBiggestAnimal(): Animal? {
        val ret = this.biggestAnimal() ?: return null
        this.animals.remove(ret)
        return ret
    }

    /**
     * Gets information whether this square is Occupied - are animals ([Animal]) in it or is grass ([Grass]) inside
     * @return True if anything is present, false otherwise
     */
    val isOcuppied: Boolean
        get() = animals.size != 0 || grass != null


}
