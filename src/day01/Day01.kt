package day01

import utils.*

fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day01_test")
    expect(part1(testInput), 0)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
