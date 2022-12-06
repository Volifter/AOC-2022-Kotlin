package day06

import utils.*

fun findUniqueSequence(str: String, size: Int) = str
    .windowed(size)
    .indexOfFirst { it.toSet().size == size } + size

fun part1(input: List<String>): Int = findUniqueSequence(input.single(), 4)

fun part2(input: List<String>): Int = findUniqueSequence(input.single(), 14)

fun main() {
    val testInput = readInput("Day06_test")
    expect(part1(testInput), 11)
    expect(part2(testInput), 26)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}
