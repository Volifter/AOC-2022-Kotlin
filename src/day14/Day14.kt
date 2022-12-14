package day14

import utils.*

val SAND_SOURCE_COORDS = Coords(500, 0)

data class Coords(var x: Int, var y: Int) {
    fun coerceIn(range: IntRange): Coords =
        Coords(x.coerceIn(range), y.coerceIn(range))

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
}

fun parseRockLines(input: List<String>): List<List<Coords>> =
    input.map { line ->
        line.split(" -> ").map { coords ->
            val (x, y) = coords.split(",").map(String::toInt)

            Coords(x, y)
        }
    }

fun getMapFromRockLines(rockLines: List<List<Coords>>): Set<Coords> {
    val map = mutableSetOf<Coords>()

    rockLines.forEach { rockLine ->
        rockLine.windowed(2).forEach { (from, to) ->
            val dir = (to - from).coerceIn(-1..1)

            map.addAll(
                generateSequence(from) { it + dir }.takeWhile { it != to + dir }
            )
        }
    }

    return map
}

fun dropSand(map: Set<Coords>, height: Int): Coords =
    generateSequence (SAND_SOURCE_COORDS) { coords ->
        listOf(
            coords + Coords(0, 1),
            coords + Coords(-1, 1),
            coords + Coords(1, 1)
        ).find { it !in map }
    }
        .takeWhile { it.y <= height }
        .last()

fun part1(input: List<String>): Int {
    val map = getMapFromRockLines(parseRockLines(input)).toMutableSet()
    val height = map.maxOf { it.y }

    return generateSequence { dropSand(map, height).also { map.add(it) } }
        .takeWhile { it.y < height }
        .count()
}

fun part2(input: List<String>): Int {
    val map = getMapFromRockLines(parseRockLines(input)).toMutableSet()
    val height = map.maxOf { it.y } + 1

    return generateSequence { dropSand(map, height).also { map.add(it) } }
        .takeWhile { SAND_SOURCE_COORDS !in map }
        .count() + 1
}

fun main() {
    val testInput = readInput("Day14_test")
    expect(part1(testInput), 24)
    expect(part2(testInput), 93)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
