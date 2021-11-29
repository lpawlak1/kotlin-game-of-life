package agh.ics.gameoflife.model

import agh.ics.gameoflife.elements.Animal
import agh.ics.gameoflife.position.Vector2d
import agh.ics.gameoflife.position.MapDirection
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class SquareTest {

    @Test
    fun `getAnimals$game_of_life_main`() {
        val square: Square = Square(Vector2d(1,3))
        square.place(Animal(Vector2d(1,3),direction = MapDirection.NORTH, life = 10))
        assertEquals(square.animals.size, 1)
        square.place(Animal(Vector2d(1,3),direction = MapDirection.SOUTH, life = 11))
        assertEquals(square.animals.size, 2)
        square.place(Animal(Vector2d(1,3),direction = MapDirection.NORTH_EAST, life = 12))
        assertEquals(square.animals.size, 3)
    }

    @Test
    fun changeDay() {
        val square: Square = Square(Vector2d(1,3))

        val animal_1 = Animal(Vector2d(1,3),MapDirection.NORTH, life = 1)
        assertTrue(square.place(animal_1))

        square.changeDay()
        assertEquals(animal_1.life, 0)
        assertEquals(animal_1.lifeSpan, 1)


        val animal_2 = Animal(Vector2d(1,3),MapDirection.SOUTH, life = 2)
        assertTrue(square.place(animal_2))


        square.changeDay()

        assertEquals(animal_1.life, -1)
        assertEquals(animal_1.lifeSpan, 2)

        assertEquals(animal_2.life, 1)
        assertEquals(animal_2.lifeSpan, 1)


        val animal_3 = Animal(Vector2d(1,3),MapDirection.NORTH_EAST, life = 3)
        assertTrue(square.place(animal_3))

        square.changeDay()
        assertEquals(animal_1.life, -2)
        assertEquals(animal_1.lifeSpan, 3)

        assertEquals(animal_2.life, 0)
        assertEquals(animal_2.lifeSpan, 2)

        assertEquals(animal_3.life, 2)
        assertEquals(animal_3.lifeSpan, 1)

    }

    @Test
    fun removeDead() {

        val square: Square = Square(Vector2d(1,3))

        val animal_1 = Animal(Vector2d(1,3),MapDirection.NORTH, life = 1)
        assertTrue(square.place(animal_1))

        square.changeDay()

        square.removeDead()
        assertEquals(square.animals.size, 0)


        val animal_2 = Animal(Vector2d(1,3),MapDirection.SOUTH, life = 2)
        assertTrue(square.place(animal_2))

        assertEquals(square.animals.size, 1)
        square.changeDay()
        square.removeDead()
        assertEquals(square.animals.size, 1)
        square.changeDay()
        assertEquals(square.animals.size, 1)

        square.removeDead()
        assertEquals(square.animals.size, 0)


        val animal_3 = Animal(Vector2d(1,3),MapDirection.NORTH_EAST, life = 3)
        assertTrue(square.place(animal_3))
        val animal_3_2 = Animal(Vector2d(1,3),MapDirection.NORTH_EAST, life = 3)
        assertTrue(square.place(animal_3_2))

        square.changeDay()
        square.removeDead()
        assertEquals(square.animals.size, 2)

        square.changeDay()
        square.removeDead()
        assertEquals(square.animals.size, 2)

        square.changeDay()
        assertEquals(square.animals.size, 2)
        square.removeDead()

        assertEquals(square.animals.size, 0)
    }

    @Test
    fun eat() {
        val square: Square = Square(Vector2d(1,3))

        val animal_1 =Animal(Vector2d(1,3),direction = MapDirection.NORTH, life = 10)
        assertTrue(square.place(animal_1))
        assertTrue(square.placeGrass())

        assertFalse(square.placeGrass())
        square.eat(2)
        assertTrue(animal_1.life == 12)

        val animal_2 =Animal(Vector2d(1,3),direction = MapDirection.NORTH, life = 12)

        square.place(animal_2)
        assertTrue(square.placeGrass())

        square.eat(2)
        assertTrue(square.animals.size == 2)
        assertEquals(animal_1.life, 13)
        assertEquals(animal_2.life, 13)
    }

    @Test
    fun place() {
        val square: Square = Square(Vector2d(1,3))
        assertTrue(square.place(Animal(Vector2d(1,3),direction = MapDirection.NORTH, life = 10)))
        assertTrue(square.place(Animal(Vector2d(1,3),direction = MapDirection.SOUTH, life = 11)))
        assertTrue(square.place(Animal(Vector2d(1,3),direction = MapDirection.NORTH_EAST, life = 12)))
    }

    @Test
    fun placeGrass() {
        val square: Square = Square(Vector2d(1,3))
        assertTrue(square.place(Animal(Vector2d(1,3),direction = MapDirection.NORTH, life = 10)))
        assertTrue(square.placeGrass())
        assertFalse(square.placeGrass())
        square.eat(1)
        assertTrue(square.placeGrass())
    }

    @Test
    fun biggestAnimal() {
        val square: Square = Square(Vector2d(1,3))

        val animal_10 = Animal(Vector2d(1,3),MapDirection.NORTH, life = 10)
        assertTrue(square.place(animal_10))
        assertEquals(square.biggestAnimal(), animal_10)

        val animal_15 = Animal(Vector2d(1,3),MapDirection.SOUTH, life = 15)
        assertTrue(square.place(animal_15))
        assertEquals(square.biggestAnimal(), animal_15)

        val animal_13 = Animal(Vector2d(1,3),MapDirection.NORTH_EAST, life = 13)
        assertTrue(square.place(animal_13))
        assertEquals(square.biggestAnimal(), animal_15)
    }

    @Test
    fun getPosition() {
        val square: Square = Square(Vector2d(2,56))
        assertEquals(square.position, Vector2d(2,56))
    }

    @Test
    fun isOcuppied() {
        val square: Square = Square(Vector2d(1,3))
        assertFalse(square.isOcuppied)
        square.place(Animal(Vector2d(1,3), life=12))
        assertTrue(square.isOcuppied)
        square.placeGrass()
        assertTrue(square.isOcuppied)



        val square2 = Square(Vector2d(1,57))
        assertFalse(square2.isOcuppied)
        square2.placeGrass()
        assertTrue(square2.isOcuppied)
    }

    @Test
    fun breed() {
    }

    @Test
    fun move() {
    }

}
