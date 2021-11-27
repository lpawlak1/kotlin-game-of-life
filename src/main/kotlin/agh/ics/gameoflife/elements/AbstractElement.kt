package agh.ics.gameoflife.elements

import agh.ics.gameoflife.position.Vector2d

abstract class AbstractElement(position: Vector2d) {
    public var position: Vector2d = position
        protected set

    override fun equals(other: Any?): Boolean = (this === other) || (other is AbstractElement && position == other.position)
    override fun hashCode(): Int = position.hashCode()
}