package day04

import utils.*

fun getRanges(input: List<String>): List<Pair<IntRange, IntRange>> {
    val regex = """^(\d+)-(\d+),(\d+)-(\d+)$""".toRegex()

    return input.map { line ->
        val (a, b, c, d) = regex.find(line)!!.groupValues
            .drop(1)
            .map(String::toInt)

        Pair(a..b, c..d)
    }
}

fun part1(input: List<String>): Int = getRanges(input).count { (a, b) ->
    a.first in b && a.last in b
        || b.first in a && b.last in a
}

fun part2(input: List<String>): Int = getRanges(input).count { (a, b) ->
    maxOf(a.first, b.first) <= minOf(a.last, b.last)
}

fun main() {
    val testInput = readInput("Day04_test")
    expect(part1(testInput), 2)
    expect(part2(testInput), 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
