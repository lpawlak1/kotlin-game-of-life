package agh.ics.gameoflife.elements

import agh.ics.gameoflife.map.IWorldMap
import agh.ics.gameoflife.position.MapDirection
import agh.ics.gameoflife.position.Vector2d
import java.util.*
import kotlin.math.round
import kotlin.random.Random


class Animal(
    position: Vector2d,
    direction: MapDirection = MapDirection.NORTH,
    life: Int,
    /**
     * Genes of animal, based on parents genes
     */
    val genes: Array<Int> = Array(32) { 0 },
    /**
     * Represents map that animal is being put to, needed for move with constraints like [agh.ics.gameoflife.map.WallWorldMap] or [agh.ics.gameoflife.map.WrappedWorldMap]
     * Can be null, then none of these constraints will be used to block Animal from moving in any direction
     */
    var map: IWorldMap? = null
) : AbstractElement(position) {

    /**
     * shows when last move of animal took place [agh.ics.gameoflife.engine.IEngine]
     */
    private var lastMove: Int = 0


    /**
     * Value that represents how many day the animal has left to live
     * Use public methods to change this property, namely [Animal.deduceDayLife], [Animal.eat],
     * also will change on [Animal.getChildEnergy]
     */
    var energy: Int = life
        private set

    /**
     * Deduces one life day from life of animal
     */
    fun deduceDayLife(moveEnergy: Int) {
        this.energy -= moveEnergy
    }

    /**
     * Adds [grassLife] to the total [energy] of animal,
     * making animal eat this grass
     */
    fun eat(grassLife: Int) {
        this.energy += grassLife
    }


    /**
     * Shows in which direction [Animal] is rotated
     * Position of Animal on map
     */
    var direction: MapDirection = direction
        private set

    /**
     * Makes string of animal, it's direction representation
     */
    override fun toString(): String {
        return direction.toString()
    }


    /**
     * Represents amount of days spent by Animal on Map, needed for statistics
     */
    var lifeSpan: Int = 0
        set(newValue) {
            if (field + 1 != newValue) return
            field = newValue
        }

    /**
     * Rotates animal based on their genes,
     * then moves if the animal should also move
     *
     * Returns true if animal moved, false otherwise.
     */
    fun move(lastMove: Int): Boolean {
        this.lastMove = lastMove

        when (val noOfRotate = genes[Random.nextInt(genes.size)]) {
            0 -> {
                val value = map?.translateVector(this.position + this.direction.toUnitVector(), this.position)
                if (value is Pair<Vector2d, Boolean>) {
                    val (position, moved) = value
                    if (moved) {
                        this.position = position
                        return true
                    }
                } else {
                    this.position = this.position + this.direction.toUnitVector()
                    return true
                }
            }
            4 -> {
                val value = map?.translateVector(this.position - this.direction.toUnitVector(), this.position)
                if (value is Pair<Vector2d, Boolean>) {
                    val (position, moved) = value
                    if (moved) {
                        this.position = position
                        return true
                    }
                } else {
                    this.position = this.position + this.direction.toUnitVector().opposite()
                    return true
                }
            }
            else -> {
                this.direction = this.direction.next(noOfRotate)
            }
        }
        return false
    }

    /**
     * Gets energy that is for child, deducing it from current [Animal.energy]
     *
     * Returns energy for its child
     */
    private fun getChildEnergy(): Double {
        val energy = (this.energy * 0.25)
        this.energy -= energy.toInt()
        return energy
    }


    companion object {
        fun getRandomGenes(): Array<Int> {
            val arr = Array(32) { 0 }
            for (i in 0 until 8){
                arr[i] = i
            }
            for (i in 8..32) {
                arr[i - 1] = Random.nextInt(0,8)
            }
            arr.sort()
            return arr
        }

        fun breed(first: Animal, second: Animal): Animal {
            val genes = getGenesFromParents(first, second)
            val childEnergy = (first.getChildEnergy() + second.getChildEnergy()).toInt()
            val childPosition = first.position

            return Animal(
                childPosition,
                MapDirection.directionFactory(Random.Default.nextInt(8)),
                childEnergy,
                genes,
                first.map
            )
        }

        private fun reWriteGenes(first: Int, last: Int, childGenes: Array<Int>, parent: Animal) {
            for (i in first until last) {
                childGenes[i] = parent.genes[i]
            }
        }

        private fun getGenesFromParents(parent1: Animal, parent2: Animal): Array<Int> {

            val childGenes = Array(32) { 0 }
            val suma = parent1.energy + parent2.energy
            val cut1: Int = round((32 * (parent1.energy).toDouble()) / suma).toInt()
            val rngWho: Boolean = Random.Default.nextInt(0, 2) == 1

            if (rngWho) {
                reWriteGenes(0, cut1, childGenes, parent1)
                reWriteGenes(cut1, 32, childGenes, parent2)
            } else {
                reWriteGenes(0, cut1, childGenes, parent2)
                reWriteGenes(cut1, 32, childGenes, parent1)
            }

            Arrays.sort(childGenes)
            return childGenes
        }
    }
}
