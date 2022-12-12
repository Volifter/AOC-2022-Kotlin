package day12

import utils.*

data class Coords(var x: Int, var y: Int) {
    operator fun plus(other: Coords): Coords = Coords(x + other.x, y + other.y)

    operator fun minus(other: Coords): Coords = Coords(x - other.x, y - other.y)
}

class Map(lines: List<String>) {
    lateinit var start: Coords

    private lateinit var end: Coords

    private val map: List<List<Int>> =
        lines.mapIndexed { y, line ->
            line.mapIndexed { x, c ->
                when (c) {
                    'S' -> {
                        start = Coords(x, y)
                        'a'
                    }
                    'E' -> {
                        end = Coords(x, y)
                        'z'
                    }
                    else -> c
                } - 'a'
            }
        }

    private val width get() = map.first().size

    private val height get() = map.size

    fun getCoordsOfHeight(height: Int): Set<Coords> =
        map.flatMapIndexed { y, row ->
            row.mapIndexedNotNull { x, h ->
                Coords(x, y).takeIf { h == height }
            }
        }.toSet()

    private fun getValidNeighbors(coords: Coords): List<Coords> =
        listOf(
            coords + Coords(1, 0),
            coords + Coords(-1, 0),
            coords + Coords(0, 1),
            coords + Coords(0, -1)
        ).filter {
            it.x in 0 until width
                && it.y in 0 until height
                && map[it.y][it.x] <= map[coords.y][coords.x] + 1
        }

    fun solve(possibleStarts: Set<Coords>): Int {
        val visited = possibleStarts.toMutableSet()
        var stack = possibleStarts.toMutableList()

        generateSequence(0) {
            (it + 1).takeIf { stack.isNotEmpty() }
        }.forEach { depth ->
            val newStack = mutableListOf<Coords>()

            stack.forEach { coords ->
                getValidNeighbors(coords).forEach neighborLoop@{ neighbor ->
                    if (neighbor in visited)
                        return@neighborLoop

                    if (neighbor == end)
                        return depth + 1

                    newStack.add(neighbor)
                    visited.add(neighbor)
                }
            }

            stack = newStack
        }

        return -1
    }
}

fun part1(input: List<String>): Int =
    Map(input).run { solve(setOf(start)) }

fun part2(input: List<String>): Int =
    Map(input).run { solve(getCoordsOfHeight(0)) }

fun main() {
    val testInput = readInput("Day12_test")
    expect(part1(testInput), 31)
    expect(part2(testInput), 29)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
