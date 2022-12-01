package day01

import utils.*

fun getElvesCalories(input: List<String>): List<Int> =
    (input + listOf("")).fold(Pair(listOf<Int>(), 0)) { (cals, n), line ->
        if (line.isEmpty())
            Pair(cals + listOf(n), 0)
        else
            Pair(cals, n + line.toInt())
    }.first

fun part1(input: List<String>): Int = getElvesCalories(input).maxOf { it }

fun part2(input: List<String>): Int =
    getElvesCalories(input).sortedDescending().take(3).sum()

fun main() {
    val testInput = readInput("Day01_test")
    expect(part1(testInput), 24000)
    expect(part2(testInput), 45000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
