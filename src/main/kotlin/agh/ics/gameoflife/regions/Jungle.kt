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
        val x = Random.nextInt(lowerLeft.x, upperRight.x + 1)
        val y = Random.nextInt(lowerLeft.y, upperRight.y + 1)
        return Vector2d(x, y)
    }

    override fun retColor(): Color {
        return Color(41, 117, 26, 255)
    }

    operator fun contains(position: Vector2d): Boolean {
        return isIn(position)
    }

    companion object {
        fun calculateJungle(width: Int, height: Int, jungle_ratio: Double): Jungle {
            val jungleWidth: Int = (width * jungle_ratio).toInt()
            val jungleHeight: Int = (height * jungle_ratio).toInt()
            val lowerLeft = Vector2d((width - jungleWidth) / 2, (height - jungleHeight) / 2)
            val upperRight = Vector2d(lowerLeft.x + jungleWidth, lowerLeft.y + jungleHeight)
            return Jungle(lowerLeft, upperRight)
        }

    }
}