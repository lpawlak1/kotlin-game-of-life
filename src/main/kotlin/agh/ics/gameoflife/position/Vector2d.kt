package agh.ics.gameoflife.position

import java.util.*
import kotlin.math.max
import kotlin.math.min

data class Vector2d(val x: Int, val y: Int) {

    override fun toString(): String = "($x,$y)"

    fun precedes(other: Vector2d): Boolean = x <= other.x && y <= other.y

    fun follows(other: Vector2d): Boolean = x >= other.x && y >= other.y

    fun upperRight(other: Vector2d): Vector2d = Vector2d(max(x, other.x), max(y, other.y))

    fun lowerLeft(other: Vector2d): Vector2d = Vector2d(min(x, other.x), min(y, other.y))

    fun opposite(): Vector2d = Vector2d(x * -1, y * -1)

    operator fun plus(other: Vector2d): Vector2d = Vector2d(x + other.x, y + other.y)

    operator fun minus(other: Vector2d): Vector2d = Vector2d(x - other.x, y - other.y)

    fun otoczka() = makeOtoczka(this)
}

fun makeOtoczka(pos: Vector2d): List<Vector2d>{
    var vec = mutableListOf<Vector2d>()
    for (i in 0..7) {
        vec.add(pos + MapDirection.NORTH.next(i).toUnitVector())
    }
    return vec
}
