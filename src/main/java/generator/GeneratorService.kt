package generator

import java.util.*

fun ClosedRange<Int>.random() = Random().nextInt(endInclusive - start) + start


class GeneratorService private constructor() {

    private object Holder {
        val INSTANCE = GeneratorService()
    }

    companion object {
        val instance: GeneratorService by lazy { Holder.INSTANCE }
    }

    fun generate(height: Int, width: Int): String {

        // generate room full of walls
        val level = Array(height, { _ ->
            Array(width, { _ -> "+" })
        })

        // walk from 5 different starting positions
        (0 until 5).forEach { randomWalk(level) }

        // place objects
        val objectPositions = listOf(
                placeObject("a", level), // goal
                placeObject("A", level), // box
                placeObject("0", level) // agent
        )

        // isSolvableLevel that level can be solved - otherwise retry level generation

        if (!isSolvableLevel(level, objectPositions)) return generate(height, width)

        return toRoom(level)
    }

    /**
     * Performs a random walk in the level.
     * Returns all visited positions.
     */
    private fun randomWalk(level: Array<Array<String>>): MutableList<Pair<Int, Int>> {
        // select random starting position
        var currentX = (2 until level[0].size - 2).random()
        var currentY = (2 until level.size - 2).random()

        // if starting position is empty, we retry
        if (level[currentY][currentX] == " ") return randomWalk(level)

        var currentDirection = (0..3).random() // 0 = north, 1 = south, 2 = east, 3 = west
        val maxSteps = 30
        val directionChangeProbability = 0.30

        // store visited positions
        val visitedPositions = mutableListOf<Pair<Int, Int>>(Pair(currentX, currentY))

        // empty starting position
        level[currentY][currentX] = " "

        (0..maxSteps).forEach {
            val (x, y) = takeStep(level, currentX, currentY, currentDirection)
            currentX = x
            currentY = y
            visitedPositions.add(Pair(x, y)) // store position
            val changeDirection = Random().nextDouble()
            if (changeDirection > 1 - directionChangeProbability) {
                // change direction
                var newDirection = (0..3).random()
                while (currentDirection == newDirection) newDirection = (0..3).random()
                currentDirection = newDirection
            }
        }

        return visitedPositions
    }

    /**
     * Takes a step in the level, in the given direction
     * Returns the new position
     */
    private fun takeStep(level: Array<Array<String>>, currentX: Int, currentY: Int, direction: Int): Pair<Int, Int> {

        var nextX = currentX
        var nextY = currentY

        // clear out at least 2 adjacent blocjs
        when (direction) {
            0 -> { // north
                if (nextY < 17) nextY++
                //level[nextY][nextX + 1] = " "
            }
            1 -> { // south
                if (nextY > 2) nextY--
                //level[nextY][nextX + 1] = " "
            }
            2 -> { // east
                if (nextX < 17) nextX++
                //level[nextY + 1][nextX] = " "
            }
            3 -> { // west
                if (nextY > 2) nextX--
                //level[nextY + 1][nextX] = " "
            }
        }

        level[nextY][nextX] = " "

        return Pair(nextX, nextY)
    }

    /**
     * Place a box, goal and an agent
     */
    private fun placeObject(letter: String, level: Array<Array<String>>): Pair<Int, Int> {

        val objectX = (2 until level[0].size - 2).random()
        val objectY = (2 until level.size - 2).random()

        // try a new position, if it is a wall
        if (level[objectY][objectX] == "+") return placeObject(letter, level)

        level[objectY][objectX] = letter

        return Pair(objectX, objectY)
    }

    /**
     * Check that level can be solved
     */
    private fun isSolvableLevel(level: Array<Array<String>>, objectPositions: List<Pair<Int, Int>>): Boolean {
        val startPosition = objectPositions.first()

        // try all reachable positions from starting position
        val reachedPositions = explore(level, startPosition, listOf(startPosition)) + listOf(startPosition)

        return objectPositions.map {
            reachedPositions.contains(it)
        }.reduce { acc, b -> acc && b }
    }

    private fun explore(level: Array<Array<String>>,
                        position: Pair<Int, Int>,
                        reachedNeighbours: List<Pair<Int, Int>>): List<Pair<Int, Int>> {

        var neighbours = listOf<Pair<Int, Int>>(
                Pair(position.first + 1, position.second),
                Pair(position.first - 1, position.second),
                Pair(position.first, position.second + 1),
                Pair(position.first, position.second - 1)
        )

        neighbours = neighbours.filter {
            it.first < 19 && it.second < 19 &&
                    it.first > 0 && it.second > 0 &&
                    level[it.second][it.first] != "+" &&
                    !reachedNeighbours.contains(it)
        }

        neighbours.forEach {
            neighbours += explore(level, it, neighbours + reachedNeighbours)
        }

        return neighbours
    }

    /**
     * Returns the string-representation of a given level matrix
     */
    private fun toRoom(level: Array<Array<String>>): String {
        var room = ""
        // each row
        level.forEach {
            // each col
            for (i in 0 until it.size) room += it[i]
            room += "\n"
        }
        return room
    }
}