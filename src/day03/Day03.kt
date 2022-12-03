package day03

import utils.*

fun findDuplicate(lines: List<Set<Char>>): Char = lines.reduce { acc, chars ->
    acc.intersect(chars)
}.single()

fun getPriority(c: Char): Int =
    1 + (c.lowercaseChar() - 'a') + c.isUpperCase().compareTo(false) * 26

fun part1(input: List<String>): Int = input.sumOf { line ->
    getPriority(findDuplicate(line.chunked(line.length / 2).map { it.toSet() }))
}

fun part2(input: List<String>): Int = input.chunked(3).sumOf { lines ->
    getPriority(findDuplicate(lines.map { it.toSet() }))
}

fun main() {
    val testInput = readInput("Day03_test")
    expect(part1(testInput), 157)
    expect(part2(testInput), 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
