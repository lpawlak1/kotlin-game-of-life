package agh.ics.gameoflife.elements

import agh.ics.gameoflife.position.MapDirection
import agh.ics.gameoflife.position.Vector2d

class Animal(position: Vector2d, direction: MapDirection = MapDirection.NORTH) : AbstractElement(position) {

    var life = 0
    public var direction = direction
        private set

    override fun toString(): String {
        return direction.toString()
    }
}