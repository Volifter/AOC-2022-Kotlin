package day24

import utils.*

val DIRECTIONS = listOf(
    Coords(0, 0),
    Coords(1, 0),
    Coords(-1, 0),
    Coords(0, 1),
    Coords(0, -1)
)

data class Coords(var x: Int, var y: Int) {
    fun wrapIn(size: Coords): Coords =
        Coords(
            Math.floorMod(x, size.x),
            Math.floorMod(y, size.y)
        )

    operator fun plus(other: Coords): Coords = Coords(x + other.x, y + other.y)

    operator fun contains(other: Coords): Boolean =
        other.x in 0 until x
            && other.y in 0 until y
}

data class Blizzard(var position: Coords, private val direction: Coords) {
    fun move(size: Coords) {
        position = (position + direction).wrapIn(size)
    }
}

class Field(input: List<String>) {
    val size: Coords = Coords(input.first().length - 2, input.size - 2)
    val start: Coords = Coords(input.first().indexOf('.') - 1, -1)
    val end: Coords = Coords(input.last().indexOf('.') - 1, size.y)

    private val blizzards: List<Blizzard> =
        input.slice(1..size.y).flatMapIndexed { y, row ->
            row.slice(1..size.x).mapIndexedNotNull { x, c ->
                when (c) {
                    '>' -> Coords(1, 0)
                    '<' -> Coords(-1, 0)
                    'v' -> Coords(0, 1)
                    '^' -> Coords(0, -1)
                    else -> null
                }?.let { Blizzard(Coords(x, y), it) }
            }
        }

    private val blizzardPositions get() = sequence {
        while (true) {
            yield(blizzards.map { it.position }.toSet())
            blizzards.forEach { it.move(size) }
        }
    }

    fun solve(targets: List<Coords>): Int {
        val targetsIterator = targets.asSequence().iterator()
        var target = targetsIterator.next()
        val blizzardsIterator = blizzardPositions.iterator()
        var blizzards = blizzardsIterator.next()

        return generateSequence(setOf(start)) { stack ->
            stack
                .flatMap { position ->
                    DIRECTIONS.mapNotNull { dir ->
                        (position + dir).takeIf {
                            (
                                it in size
                                    || it == start
                                    || it == end
                                ) && it !in blizzards
                        }
                    }
                }
                .let { nextStack ->
                    blizzards = blizzardsIterator.next()

                    if (target in nextStack)
                        setOf(target)
                            .takeIf { targetsIterator.hasNext() }
                            ?.also { target = targetsIterator.next() }
                    else
                        nextStack
                }
                ?.toSet()
        }.count() - 1
    }
}

fun part1(input: List<String>): Int =
    Field(input).run { solve(listOf(end)) }

fun part2(input: List<String>): Int =
    Field(input).run { solve(listOf(end, start, end)) }

fun main() {
    val testInput = readInput("Day24_test")
    expect(part1(testInput), 18)
    expect(part2(testInput), 54)

    val input = readInput("Day24")
    println(part1(input))
    println(part2(input))
}
