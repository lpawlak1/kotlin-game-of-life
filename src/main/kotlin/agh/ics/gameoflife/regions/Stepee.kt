package agh.ics.gameoflife.regions

import agh.ics.gameoflife.position.Vector2d
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

class Stepee(
    private val externalLowerLeft: Vector2d,
    private val externalUpperRight: Vector2d,
    private val lowerLeft: Vector2d,
    private val upperRight: Vector2d
) : IRegion {

    override fun isIn(test: Vector2d): Boolean {
        return !(test.follows(lowerLeft) && test.precedes(upperRight))
                && (test.follows(externalLowerLeft) && test.precedes(externalUpperRight))
    }

    override fun getRandomVector(): Vector2d {
        lateinit var rngVector: Vector2d
        do {
            val x = Random.nextInt(externalLowerLeft.x, externalUpperRight.x + 1)
            val y = Random.nextInt(externalLowerLeft.y, externalUpperRight.y + 1)
            rngVector = Vector2d(x, y)

        } while (!this.isIn(rngVector))

        return rngVector
    }

    override fun retColor(): Color {
        return Color(255, 175, 104, 255)
    }

    operator fun contains(vec: Vector2d): Boolean {
        return this.isIn(vec)
    }
}