package agh.ics.gameoflife.elements

import agh.ics.gameoflife.position.MapDirection
import agh.ics.gameoflife.position.Vector2d
import org.junit.jupiter.api.Test

internal class AnimalTest {

    @Test
    fun getLife() {
        val alfa = Animal(Vector2d(1,1), life=0)
        assert(alfa.energy == 0)
        assert(alfa.energy != 9)
    }

    @Test
    fun setLife() {
        val alfa = Animal(Vector2d(1,1), life=10)
        assert(alfa.energy == 10)
        assert(alfa.energy != 4)
        alfa.deduceDayLife(1)
        assert(alfa.energy == 9)
    }

    @Test
    fun getDirection() {
        val alfa = Animal(Vector2d(1,9), life=0)
        assert(alfa.position == Vector2d(1,9))
        assert(alfa.position == Vector2d(1,9))
    }

    @Test
    fun testToString() {
        val alfa = Animal(Vector2d(1,9), life=0)
        assert(alfa.direction == MapDirection.NORTH)
        assert(alfa.direction != MapDirection.WEST)
    }
}