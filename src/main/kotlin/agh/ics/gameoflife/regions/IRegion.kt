package agh.ics.gameoflife.regions

import agh.ics.gameoflife.position.Vector2d
import androidx.compose.ui.graphics.Color

interface IRegion {
    /**
     * Checks whether given Vector2d is in this region
     * @param test Vector2d to be tested
     * @return true if test is in this region, false otherwise
     */
    fun isIn(test: Vector2d): Boolean

    /**
     * Returns random vector that is in this area
     */
    fun getRandomVector(): Vector2d


    /**
     * Returns [Color] that matches the IRegion specification, for background coloring in View
     */
    fun retColor(): Color
}