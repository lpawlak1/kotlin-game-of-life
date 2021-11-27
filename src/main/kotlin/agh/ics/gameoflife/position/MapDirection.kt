package agh.ics.gameoflife.position

enum class MapDirection(val `val`: Int, private val rName: String, private val x: Int, private val y: Int) {
    NORTH(0, "NN", 0, 1),
    NORTH_EAST(1, "NE", 1, 1),
    EAST(2, "EE", 1, 0),
    SOUTH_EAST(3, "SE", 1, -1),
    SOUTH(4,"SS",0,-1),
    SOUTH_WEST(5, "SW", -1, -1),
    WEST(6, "WW", -1, 0),
    NORTH_WEST(7, "NW", -1, 1);

    override fun toString(): String = rName

    fun next(how_many: Int): MapDirection {
        return directionFactory((`val` + how_many) % 8)
    }

    fun previous(how_many: Int): MapDirection {
        return directionFactory((`val` - how_many + 8) % 8)
    }

    fun toUnitVector(): Vector2d {
        return Vector2d(x, y)
    }

    companion object {
        fun directionFactory(`val`: Int): MapDirection {
            return values().firstOrNull { e: MapDirection -> e.`val` == `val` } ?: NORTH
        }
    }
}