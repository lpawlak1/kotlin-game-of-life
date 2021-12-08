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

        square.changeDay(1)
        assertEquals(animal_1.energy, 0)
        assertEquals(animal_1.lifeSpan, 1)


        val animal_2 = Animal(Vector2d(1,3),MapDirection.SOUTH, life = 2)
        assertTrue(square.place(animal_2))


        square.changeDay(1)

        assertEquals(animal_1.energy, -1)
        assertEquals(animal_1.lifeSpan, 2)

        assertEquals(animal_2.energy, 1)
        assertEquals(animal_2.lifeSpan, 1)


        val animal_3 = Animal(Vector2d(1,3),MapDirection.NORTH_EAST, life = 3)
        assertTrue(square.place(animal_3))

        square.changeDay(1)
        assertEquals(animal_1.energy, -2)
        assertEquals(animal_1.lifeSpan, 3)

        assertEquals(animal_2.energy, 0)
        assertEquals(animal_2.lifeSpan, 2)

        assertEquals(animal_3.energy, 2)
        assertEquals(animal_3.lifeSpan, 1)

    }

    @Test
    fun removeDead() {

        val square: Square = Square(Vector2d(1,3))

        val animal_1 = Animal(Vector2d(1,3),MapDirection.NORTH, life = 1)
        assertTrue(square.place(animal_1))

        square.changeDay(1)

        square.removeDead()
        assertEquals(square.animals.size, 0)


        val animal_2 = Animal(Vector2d(1,3),MapDirection.SOUTH, life = 2)
        assertTrue(square.place(animal_2))

        assertEquals(square.animals.size, 1)
        square.changeDay(1)
        square.removeDead()
        assertEquals(square.animals.size, 1)
        square.changeDay(1)
        assertEquals(square.animals.size, 1)

        square.removeDead()
        assertEquals(square.animals.size, 0)


        val animal_3 = Animal(Vector2d(1,3),MapDirection.NORTH_EAST, life = 3)
        assertTrue(square.place(animal_3))
        val animal_3_2 = Animal(Vector2d(1,3),MapDirection.NORTH_EAST, life = 3)
        assertTrue(square.place(animal_3_2))

        square.changeDay(1)
        square.removeDead()
        assertEquals(square.animals.size, 2)

        square.changeDay(1)
        square.removeDead()
        assertEquals(square.animals.size, 2)

        square.changeDay(1)
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
        assertTrue(animal_1.energy == 12)

        val animal_2 =Animal(Vector2d(1,3),direction = MapDirection.NORTH, life = 12)

        square.place(animal_2)
        assertTrue(square.placeGrass())

        square.eat(2)
        assertTrue(square.animals.size == 2)
        assertEquals(animal_1.energy, 13)
        assertEquals(animal_2.energy, 13)
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
        assertFalse(square.isOccupied)
        square.place(Animal(Vector2d(1,3), life=12))
        assertTrue(square.isOccupied)
        square.placeGrass()
        assertTrue(square.isOccupied)



        val square2 = Square(Vector2d(1,57))
        assertFalse(square2.isOccupied)
        square2.placeGrass()
        assertTrue(square2.isOccupied)
    }

    @Test
    fun breed() {
        val square: Square = Square(Vector2d(1,3))
        square.place(Animal(Vector2d(1,3), life = 50, genes=Array(32){0}))
        square.place(Animal(Vector2d(1,3), life = 150, genes=Array(32){4}))
        square.breed(40)
        val parent_a = square.pollBiggestAnimal()!!
        assertEquals(parent_a.energy, (150- (150*0.25).toInt()).toInt())
        val child = square.pollBiggestAnimal()!!
        assertEquals(50, child.energy)
        val parent_b = square.pollBiggestAnimal()!!
        assertEquals(parent_b.energy, (50- (50*0.25).toInt()))
    }

    @Test
    fun move() {
        val square: Square = Square(Vector2d(1,3))
        square.place(Animal(Vector2d(1,3), life = 10))
        square.place(Animal(Vector2d(1,3), life = 10, genes=Array(32){4}))
        square.place(Animal(Vector2d(1,3), life = 10, genes=Array(32){7}))
        square.place(Animal(Vector2d(1,3), life = 10, genes=Array(32){3}))

        val animals = square.move(10)
        assertEquals(2, animals.size) // The first 2 will move

    }

}
