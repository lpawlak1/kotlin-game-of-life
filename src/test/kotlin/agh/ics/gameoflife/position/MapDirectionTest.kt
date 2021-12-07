package agh.ics.gameoflife.position

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MapDirectionTest {

    @Test
    operator fun next() {
        assert(MapDirection.NORTH.next(1) == MapDirection.NORTH_EAST)
        assert(MapDirection.NORTH_EAST.next(1) == MapDirection.EAST)
        assert(MapDirection.EAST.next(1) == MapDirection.SOUTH_EAST)
        assert(MapDirection.SOUTH_EAST.next(1) == MapDirection.SOUTH)
        assert(MapDirection.SOUTH.next(1) == MapDirection.SOUTH_WEST)
        assert(MapDirection.SOUTH_WEST.next(1) == MapDirection.WEST)
        assert(MapDirection.WEST.next(1) == MapDirection.NORTH_WEST)
        assert(MapDirection.NORTH_WEST.next(1) == MapDirection.NORTH)

        assert(MapDirection.NORTH.next(8) == MapDirection.NORTH)
        assert(MapDirection.NORTH.next(9) == MapDirection.NORTH_EAST)
        assert(MapDirection.NORTH.next(10) == MapDirection.EAST)
        assert(MapDirection.NORTH.next(11) == MapDirection.SOUTH_EAST)
        assert(MapDirection.NORTH.next(12) == MapDirection.SOUTH)
        assert(MapDirection.NORTH.next(13) == MapDirection.SOUTH_WEST)
    }

    @Test
    fun previous() {
        assert(MapDirection.NORTH == MapDirection.NORTH_EAST.previous(1))
        assert(MapDirection.NORTH_EAST == MapDirection.EAST.previous(1))
        assert(MapDirection.EAST == MapDirection.SOUTH_EAST.previous(1))
        assert(MapDirection.SOUTH_EAST == MapDirection.SOUTH.previous(1))
        assert(MapDirection.SOUTH == MapDirection.SOUTH_WEST.previous(1))
        assert(MapDirection.SOUTH_WEST == MapDirection.WEST.previous(1))
        assert(MapDirection.WEST == MapDirection.NORTH_WEST.previous(1))
        assert(MapDirection.NORTH_WEST == MapDirection.NORTH.previous(1))

        assert(MapDirection.NORTH== MapDirection.NORTH.previous(8))
        assert(MapDirection.NORTH== MapDirection.NORTH_EAST.previous(9))
        assert(MapDirection.NORTH == MapDirection.EAST.previous(10))
        assert(MapDirection.NORTH == MapDirection.SOUTH_EAST.previous(11))
        assert(MapDirection.NORTH == MapDirection.SOUTH.previous(12))
        assert(MapDirection.NORTH == MapDirection.SOUTH_WEST.previous(13))
    }

    @Test
    fun toUnitVector() {
        assert(MapDirection.NORTH.toUnitVector() == Vector2d(0, 1))
        assert(MapDirection.NORTH_EAST.toUnitVector() == Vector2d(1, 1))
        assert(MapDirection.EAST.toUnitVector() == Vector2d(1, 0))
        assert(MapDirection.SOUTH_EAST.toUnitVector() == Vector2d(1, -1))
        assert(MapDirection.SOUTH.toUnitVector() == Vector2d(0,-1))
        assert(MapDirection.SOUTH_WEST.toUnitVector() == Vector2d(-1, -1))
        assert(MapDirection.WEST.toUnitVector() == Vector2d(-1, 0))
        assert(MapDirection.NORTH_WEST.toUnitVector() == Vector2d(-1, 1))
    }
}
