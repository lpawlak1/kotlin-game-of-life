package agh.ics.gameoflife.view

import agh.ics.gameoflife.elements.AbstractElement
import agh.ics.gameoflife.position.Vector2d

interface ITopElementChanged {
    fun elementChanged(element: AbstractElement?, position: Vector2d)
    fun addObserver(observer: ITopElementObserver)
}

interface ITopElementObserver {
    fun notify(element: AbstractElement?, position: Vector2d)
}
