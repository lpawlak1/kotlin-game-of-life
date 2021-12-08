package agh.ics.gameoflife.position

enum class MapDirection(val `val`: Int, private val rName: String, private val x: Int, private val y: Int) {
    NORTH(0, "↑", 0, 1),
    NORTH_EAST(1, "↗", 1, 1),
    EAST(2, "→", 1, 0),
    SOUTH_EAST(3, "↘", 1, -1),
    SOUTH(4, "↓", 0, -1),
    SOUTH_WEST(5, "↙", -1, -1),
    WEST(6, "←", -1, 0),
    NORTH_WEST(7, "↖", -1, 1);

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