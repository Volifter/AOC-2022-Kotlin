package day05

import utils.*

data class Instruction(val amount: Int, val from: Int, val to: Int)

fun parseCrates(input: List<String>): List<List<Char>> {
    val count = (input.first().length + 1) / 4

    return (0 until count).map { i ->
        input.mapNotNull { line -> line[1 + i * 4].takeIf { it != ' ' } }
    }
}

fun parseInstructions(input: List<String>): List<Instruction> {
    val regex = """move (\d+) from (\d+) to (\d+)""".toRegex()

    return input.map { line ->
        val (amount, from, to) = regex.find(line)!!.groupValues
            .drop(1)
            .map(String::toInt)

        Instruction(amount, from - 1, to - 1)
    }
}

fun parseInput(input: List<String>): Pair<List<List<Char>>, List<Instruction>> {
    val sepIdx = input.indexOf("")
    val crates = parseCrates(input.take(sepIdx - 1))
    val instructions = parseInstructions(input.drop(sepIdx + 1))

    return Pair(crates, instructions)
}

fun executeInstructions(
    crates: MutableList<List<Char>>,
    instructions: List<Instruction>,
    isReversed: Boolean
): String {
    instructions.forEach {
        val batch = crates[it.from].take(it.amount).let { batch ->
            batch.takeIf { isReversed }?.reversed() ?: batch
        }

        crates[it.from] = crates[it.from].drop(it.amount)
        crates[it.to] = batch + crates[it.to]
    }

    return crates.joinToString("") { "" + it.first() }
}

fun part1(input: List<String>): String {
    val (crates, instructions) = parseInput(input)

    return executeInstructions(crates.toMutableList(), instructions, true)
}

fun part2(input: List<String>): String {
    val (crates, instructions) = parseInput(input)

    return executeInstructions(crates.toMutableList(), instructions, false)
}

fun main() {
    val testInput = readInput("Day05_test")
    expect(part1(testInput), "CMZ")
    expect(part2(testInput), "MCD")

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
