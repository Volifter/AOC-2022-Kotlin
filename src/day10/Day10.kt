package day10

import utils.*
import kotlin.math.absoluteValue

val THRESHOLDS = (20..220 step 40)

const val WIDTH = 40

fun getOperations(input: List<String>): List<Pair<Int, Int>> =
    input.map { line ->
        val args = line.split(" ")

        when (args.first()) {
            "noop" -> Pair(1, 0)
            "addx" -> Pair(2, args[1].toInt())
            else -> throw Error("invalid operation ${args.first()}")
        }
    }

fun getXValues(commands: List<Pair<Int, Int>>): List<Int> {
    val values = mutableListOf<Int>()
    var x = 1

    commands.forEach { (duration, delta) ->
        values.addAll(List(duration) { x })

        x += delta
    }

    return values
}

fun part1(input: List<String>): Int {
    val values = getXValues(getOperations(input))

    return THRESHOLDS.sumOf { i -> values[i - 1] * i }
}

fun part2(input: List<String>): List<String> =
    getXValues(getOperations(input))
        .mapIndexed { i, x -> (i % WIDTH - x).absoluteValue < 2 }
        .joinToString("") { if (it) "*" else " " }
        .chunked(WIDTH)

fun main() {
    val testInput = readInput("Day10_test")
    expect(part1(testInput), 13140)
    expect(part2(testInput), listOf(
        "**  **  **  **  **  **  **  **  **  **  ",
        "***   ***   ***   ***   ***   ***   *** ",
        "****    ****    ****    ****    ****    ",
        "*****     *****     *****     *****     ",
        "******      ******      ******      ****",
        "*******       *******       *******     "
    ))

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input).joinToString("\n"))
}
