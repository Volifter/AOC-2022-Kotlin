package day02

import utils.*

val SHAPES = mapOf(
    'A' to 'R',
    'B' to 'P',
    'C' to 'S',
    'X' to 'R',
    'Y' to 'P',
    'Z' to 'S'
)

const val ORDER = "RPS"

const val RESULTS = "XYZ"

fun getGames(input: List<String>): List<Pair<Char, Char>> =
    input.map { line -> Pair(line.first(), line.last()) }

fun part1(input: List<String>): Int = getGames(input).sumOf { (other, self) ->
    val selfTurn = SHAPES[self]!!
    val otherTurn = SHAPES[other]!!
    val selfOrder = ORDER.indexOf(selfTurn)
    val otherOrder = ORDER.indexOf(otherTurn)
    val turnScore = selfOrder + 1
    val winScore = 3 +
        (if (selfOrder == (otherOrder + 1) % ORDER.length) 3 else 0) +
        (if (otherOrder == (selfOrder + 1) % ORDER.length) -3 else 0)

    turnScore + winScore
}

fun part2(input: List<String>): Int = getGames(input).sumOf { (other, result) ->
    val otherTurn = SHAPES[other]!!
    val otherTurnOrder = ORDER.indexOf(otherTurn)
    val resultIdx = RESULTS.indexOf(result)
    val winScore = resultIdx * 3
    val delta = resultIdx - 1
    val turnScore = (otherTurnOrder + delta + ORDER.length) % ORDER.length + 1

    winScore + turnScore
}

fun main() {
    val testInput = readInput("Day02_test")
    expect(part1(testInput), 15)
    expect(part2(testInput), 12)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
