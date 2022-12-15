package day15

import utils.*
import kotlin.math.absoluteValue

val COORDS_REGEX = """x=(-?\d+), y=(-?\d+)""".toRegex()

data class Coords(var x: Int, var y: Int) {
    val manhattanDelta: Int get() = x.absoluteValue + y.absoluteValue

    operator fun plus(other: Coords): Coords = Coords(x + other.x, y + other.y)

    operator fun minus(other: Coords): Coords = Coords(x - other.x, y - other.y)

    override operator fun equals(other: Any?): Boolean =
        other is Coords
            && other.x == x
            && other.y == y

    override fun hashCode(): Int = x * 31 + y
}

fun parseInput(input: List<String>): List<Pair<Coords, Int>> =
    input
        .flatMap { line ->
            val (sensorCoords, beaconCoords) = COORDS_REGEX
                .findAll(line)
                .map { match ->
                    val (x, y) = match.groupValues.drop(1).map(String::toInt)

                    Coords(x, y)
                }.toList()
            val sensorRange = (sensorCoords - beaconCoords).manhattanDelta

            listOf(
                Pair(sensorCoords, sensorRange),
                Pair(beaconCoords, 0)
            )
        }
        .distinct()

fun getSensorRangesOnRow(
    sensors: List<Pair<Coords, Int>>,
    y: Int
): List<IntRange> =
    sensors
        .mapNotNull { (sensor, range) ->
            val delta = range - (sensor.y - y).absoluteValue

            (sensor.x - delta..sensor.x + delta).takeUnless(IntRange::isEmpty)
        }
        .sortedBy { it.first }

fun getMergedRangesOnRow(
    sensors: List<Pair<Coords, Int>>,
    y: Int
): List<IntRange> =
    getSensorRangesOnRow(sensors, y).fold(listOf()) { ranges, range ->
        ranges
            .lastOrNull()
            ?.takeIf { range.first <= it.last + 1 }
            ?.let { prev ->
                ranges.dropLast(1) + listOf(
                    (prev.first..maxOf(prev.last, range.last))
                )
            }
            ?: (ranges + listOf(range))
    }

fun splitRowAt(ranges: List<IntRange>, i: Int): List<IntRange> =
    ranges.flatMap { range ->
        if (i in range)
            listOf(
                (range.first until i),
                (i + 1..range.last)
            ).filter { !it.isEmpty() }
        else
            listOf(range)
    }

fun part1(input: List<String>, y: Int): Int {
    val sensors = parseInput(input)
    var ranges = getMergedRangesOnRow(sensors, y)

    sensors.forEach { (coords, _) ->
        if (coords.y == y)
            ranges = splitRowAt(ranges, coords.x)
    }

    return ranges.sumOf { it.last - it.first + 1 }
}

fun getHoleFrom(ranges: List<IntRange>, start: Int): Int =
    ranges
        .dropWhile { it.last < start }
        .firstOrNull()
        ?.takeIf { it.first <= start }
        ?.let { it.last + 1 }
        ?: start

fun part2(input: List<String>, size: Int): Long {
    val sensors = parseInput(input)

    return (0..size)
        .mapNotNull { y ->
            val ranges = getMergedRangesOnRow(sensors, y)
            val x = getHoleFrom(ranges, 0)

            Coords(x, y).takeIf { x <= size }
        }
        .single()
        .let { it.x * 4000000L + it.y }
}

fun main() {
    val testInput = readInput("Day15_test")
    expect(part1(testInput, 10), 26)
    expect(part2(testInput, 20), 56000011)

    val input = readInput("Day15")
    println(part1(input, 2000000))
    println(part2(input, 4000000))
}
