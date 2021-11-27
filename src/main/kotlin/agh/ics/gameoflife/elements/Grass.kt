package agh.ics.gameoflife.elements

import agh.ics.gameoflife.position.Vector2d


class Grass(position: Vector2d) : AbstractElement(position) {
    override fun toString(): String {
        return "*"
    }
}