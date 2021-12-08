package agh.ics.gameoflife.regions

import agh.ics.gameoflife.position.Vector2d
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

/**
 * Central region with jungle inside
 */
class Jungle(
    val lowerLeft: Vector2d,
    val upperRight: Vector2d
) : IRegion {

    override fun isIn(test: Vector2d): Boolean {
        return test.follows(lowerLeft) && test.precedes(upperRight)
    }

    override fun getRandomVector(): Vector2d {
        val x = Random.nextInt(lowerLeft.x, upperRight.x)
        val y = Random.nextInt(lowerLeft.y, upperRight.y)
        return Vector2d(x, y)
    }

    override fun retColor(): Color {
        return Color(255, 175, 104, 255)
    }

    operator fun contains(position: Vector2d): Boolean {
        return isIn(position)
    }
}