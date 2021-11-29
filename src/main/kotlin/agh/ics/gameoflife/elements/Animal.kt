package agh.ics.gameoflife.elements

import agh.ics.gameoflife.position.MapDirection
import agh.ics.gameoflife.position.Vector2d
import kotlin.random.Random

class Animal(position: Vector2d, direction: MapDirection = MapDirection.NORTH, life: Int) : AbstractElement(position) {

    /**
    * shows when last move of animal took place [Engine.iteration]
    */
    public var lastMove: Int = 0


    /**
     * Value that represents how many day the animal has left to live
     * You can add to life whatever you want, but if u want to deduct life only 1 lower will pass
     */
    var life: Int = life
        set(newValue) {
            if (newValue > field) {
                field = newValue
                return
            }

            if(field - 1 != newValue) return
            field = newValue
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
    private var genes: Array<Int> = Array(32){ 0 }


    /**
     * Rotates animal based on their genes
     */
    public fun rotate(){
        val no_of_rotate = genes[Random.nextInt(genes.size)]
        this.direction.next(no_of_rotate)
    }

    /**
     * Represents amount of days spent by Animal on Map, needed for statistics
     */
    public var lifeSpan: Int = 0
        set(newValue) {
            if(field + 1 != newValue) return
            field = newValue
        }

    public fun move(){
        this.lastMove++
        rotate()
        this.position = this.position + this.direction.toUnitVector()
    }

    public fun eat(energy: Int){
        this.life += energy
    }

    public fun breed(other: Animal): Animal{
        //Perform amazing operations to breed
        return Animal(this.position, life=this.life)
    }
}
