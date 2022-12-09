package day09

import utils.*
import kotlin.math.sqrt

data class Coords(var x: Int, var y: Int) {
    val delta: Float get() = sqrt((x * x + y * y) * 1f)

    operator fun plus(other: Coords): Coords = Coords(x + other.x, y + other.y)

    operator fun minus(other: Coords): Coords = Coords(x - other.x, y - other.y)
}

fun parseInput(input: List<String>): List<Pair<Coords, Int>> =
    input.map { line ->
        val (dirName, amount) = line.split(" ")
        val dir = when(dirName.single()) {
            'R' -> Coords(1, 0)
            'L' -> Coords(-1, 0)
            'U' -> Coords(0, 1)
            'D' -> Coords(0, -1)
            else -> throw Error("invalid direction $dirName")
        }

        Pair(dir, amount.toInt())
    }

fun countVisitedCells(
    movements: List<Pair<Coords, Int>>,
    chainLength: Int
): Int {
    val chain = MutableList(chainLength) { Coords(0, 0) }
    val visited = mutableSetOf(chain.last())

    movements.forEach { (delta, amount) ->
        repeat(amount) {
            chain[0] += delta

            chain.indices.windowed(2) { (leadingIdx, followingIdx) ->
                val diff = chain[leadingIdx] - chain[followingIdx]

                if (diff.delta >= 2) {
                    chain[followingIdx] += Coords(
                        diff.x.coerceIn(-1..1),
                        diff.y.coerceIn(-1..1)
                    )
                }
            }

            visited.add(chain.last())
        }
    }

    return visited.size
}

fun part1(input: List<String>): Int = countVisitedCells(parseInput(input), 2)

fun part2(input: List<String>): Int = countVisitedCells(parseInput(input), 10)

fun main() {
    val testInputA = readInput("Day09_test_a")
    val testInputB = readInput("Day09_test_b")
    expect(part1(testInputA), 13)
    expect(part2(testInputB), 36)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
