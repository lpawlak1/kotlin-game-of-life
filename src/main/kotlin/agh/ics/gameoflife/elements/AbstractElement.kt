package agh.ics.gameoflife.elements

import agh.ics.gameoflife.position.Vector2d

/**
 * Abstract class with basic like [hashCode] or [equals]
 * Things that are contained in Map should extend this class
 * Also has [position] with protected setter
 */
abstract class AbstractElement(position: Vector2d) {
    /**
     * Position of Element in Map -> type: [Vector2d]
     * Public getter and private setter
     */
    var position: Vector2d = position
        protected set

    override fun equals(other: Any?): Boolean =
        (this === other) || (other is AbstractElement && position == other.position)

    override fun hashCode(): Int = position.hashCode()
}