package agh.ics.gameoflife.regions

import agh.ics.gameoflife.position.Vector2d
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

class Stepee(
    val externalLowerLeft: Vector2d,
    val externalUpperRight: Vector2d,
    val lowerLeft: Vector2d,
    val upperRight: Vector2d
) : IRegion {

    override fun isIn(test: Vector2d): Boolean {
        return !(test.follows(lowerLeft) && test.precedes(upperRight))
                && (test.follows(externalLowerLeft) && test.precedes(externalUpperRight))
    }

    override fun getRandomVector(): Vector2d {
        lateinit var rngVector: Vector2d
        do {
            val x = Random.nextInt(externalLowerLeft.x, externalUpperRight.x)
            val y = Random.nextInt(externalLowerLeft.y, externalUpperRight.y)
            rngVector = Vector2d(x, y)

        } while (!this.isIn(rngVector))

        return rngVector
    }

    override fun retColor(): Color {
        return Color(157, 66, 21, 255)
    }
}