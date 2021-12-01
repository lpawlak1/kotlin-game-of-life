package agh.ics.gameoflife.elements

import agh.ics.gameoflife.position.MapDirection
import agh.ics.gameoflife.position.Vector2d
import java.util.*
import kotlin.math.round
import kotlin.random.Random


class Animal(position: Vector2d,
             direction: MapDirection = MapDirection.NORTH,
             life: Int,
             genes: Array<Int> = Array(32){0}
    ) : AbstractElement(position) {

    /**
    * shows when last move of animal took place [Engine.iteration]
    */
    public var lastMove: Int = 0


    /**
     * Value that represents how many day the animal has left to live
     * Use public methods to change this property, namely [Animal.deduceDayLife], [Animal.eat],
     * also will change on [Animal.getChildEnergy]
     */
    var life: Int = life
        private set

    /**
     * Deduces one life day from life of animal
     */
    public fun deduceDayLife(moveEnergy: Int){
        this.life -= moveEnergy
    }

    /**
     * Adds [grassLife] to the total [life] of animal,
     * making animal eat this grass
     */
    public fun eat(grassLife: Int) {
        this.life += grassLife
    }


    /**
     * Position of Animal on map, type: [Vector2d]
     */
    public var direction = direction
        private set

    /**
     * Makes string of animal, it's direction reprezentation
     */
    override fun toString(): String {
        return direction.toString()
    }

    /**
     * Genes of animal, based on parents genes
     */
    private var genes: Array<Int> = genes



    /**
     * Represents amount of days spent by Animal on Map, needed for statistics
     */
    public var lifeSpan: Int = 0
        set(newValue) {
            if(field + 1 != newValue) return
            field = newValue
        }

    /**
     * Rotates animal based on their genes,
     * then moves if the animal should also move
     *
     * Returns true if animal moved, false otherwise.
     */
    public fun move(lastMove: Int) : Boolean{
        this.lastMove = lastMove

        val no_of_rotate = genes[Random.nextInt(genes.size)]

        this.direction.next(no_of_rotate)

        when(no_of_rotate) {
            0 -> {
                this.position = this.position + this.direction.toUnitVector()
                return true
            }
            4 -> {
                this.position = this.position + this.direction.toUnitVector().opposite()
                return true
            }
        }
        return false
    }

    /**
     * Gets energy that is for child, deducing it from current [Animal.life]
     *
     * Returns energy for its child
     */
    private fun getChildEnergy(): Int{
        val energy: Int = (this.life*0.25).toInt()
        this.life -= energy
        return energy
    }


    companion object {
        public fun breed(first:Animal, second:Animal): Animal{
            //Perform amazing operations to breed
            val genes = getGenesFromParents(first, second)
            val child_energy = first.getChildEnergy() + second.getChildEnergy()
            val child_position = first.position

            return Animal(child_position,
                          MapDirection.directionFactory(Random.Default.nextInt(8)),
                          child_energy,
                          genes)
        }
        internal fun getGenesFromParents(parent1: Animal, parent2: Animal): Array<Int> {
            val childGenes = Array(32){ 0 }

            val suma = parent1.life + parent2.life

            val prec1: Int = round((parent1.life/suma).toDouble()).toInt()

            for (i in 0 until prec1){
                childGenes[i] = parent1.genes[i]
            }

            for (i in prec1..32){
                childGenes[i] = parent2.genes[i]
            }

            Arrays.sort(childGenes)
            return childGenes
        }
    }
}
