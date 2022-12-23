package day23

import utils.*

val DIRECTIONS = listOf(
    (-1..1).map { Coords(it, -1) },
    (-1..1).map { Coords(it, 1) },
    (-1..1).map { Coords(-1, it) },
    (-1..1).map { Coords(1, it) }
)

data class Coords(var x: Int, var y: Int) {
    operator fun plus(other: Coords): Coords = Coords(x + other.x, y + other.y)

    operator fun minus(other: Coords): Coords = Coords(x - other.x, y - other.y)

    operator fun contains(other: Coords): Boolean =
        other.x in 0 until x
            && other.y in 0 until y

    override operator fun equals(other: Any?): Boolean =
        other is Coords
            && other.x == x
            && other.y == y

    override fun hashCode(): Int = x * 31 + y

    fun minCoords(other: Coords) = Coords(minOf(x, other.x), minOf(y, other.y))

    fun maxCoords(other: Coords) = Coords(maxOf(x, other.x), maxOf(y, other.y))
}

data class Elf(var position: Coords) {
    var target: Coords? = null

    fun aim(map: Map<Coords, Elf>, i: Int): Boolean {
        target = null

        val willMove = (-1..1).any { y ->
            (-1..1 step if (y == 0) 2 else 1).any { x ->
                position + Coords(x, y) in map
            }
        }

        if (!willMove)
            return false

        (0..3).forEach { offset ->
            val directions = DIRECTIONS[(offset + i) % 4]

            if (directions.all { position + it !in map }) {
                target = position + directions[1]
                return true
            }
        }

        return false
    }
}

fun parseElves(input: List<String>): List<Elf> =
    input.flatMapIndexed { y, line ->
        line.mapIndexedNotNull { x, c ->
            if (c == '#') Elf(Coords(x, y)) else null
        }
    }

fun solve(elves: List<Elf>): Sequence<Boolean> {
    val map = elves.associateBy { it.position }.toMutableMap()
    val targets = mutableMapOf<Coords, Int>()
    var i = 0

    return generateSequence {
        var moved = false

        elves
            .onEach { elf ->
                if (elf.aim(map, i))
                    targets[elf.target!!] = (targets[elf.target] ?: 0) + 1
            }
            .forEach { elf ->
                if (elf.target?.let { targets[it] } == 1) {
                    elf.position = elf.target!!
                    moved = true
                }
            }

        targets.clear()
        map.clear()
        elves.associateByTo(map) { it.position }
        i++

        moved
    }
}

fun part1(input: List<String>): Int {
    val elves = parseElves(input)

    solve(elves).take(10).count()

    elves.map { it.position }.run {
        val min = reduce { acc, coords -> acc.minCoords(coords) }
        val max = reduce { acc, coords -> acc.maxCoords(coords) }
        val area = max - min + Coords(1, 1)

        return area.x * area.y - elves.size
    }
}

fun part2(input: List<String>): Int =
    solve(parseElves(input)).indexOfFirst { !it } + 1

fun main() {
    val testInput = readInput("Day23_test")
    expect(part1(testInput), 110)
    expect(part2(testInput), 20)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}
