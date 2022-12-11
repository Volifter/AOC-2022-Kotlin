package day11

import utils.*

data class Monkey(
    val id: Int,
    var items: MutableList<Int>,
    val operation: Char,
    val operationValue: Int?,
    val divisor: Int
) {
    lateinit var divisibleTargetMonkey: Monkey
    lateinit var indivisibleTargetMonkey: Monkey

    var inspections: Int = 0

    private fun doOperation(item: Int): Long {
        val other = operationValue ?: item

        return when (operation) {
            '+' -> item.toLong() + other
            '-' -> item.toLong() - other
            '*' -> item.toLong() * other
            '/' -> item.toLong() / other
            '%' -> item.toLong() % other
            else -> throw Error("invalid operation $operation")
        }
    }

    fun throwItems(lcm: Int, inspectionDivisor: Int) {
        inspections += items.size

        items.forEach { value ->
            val newValue = (
                doOperation(value) / inspectionDivisor % lcm
            ).toInt()
            val targetMonkey = if (newValue % divisor == 0)
                divisibleTargetMonkey
            else
                indivisibleTargetMonkey

            targetMonkey.items.add(newValue)
        }

        items = mutableListOf()
    }
}

fun readMonkeys(input: List<String>): List<Monkey> {
    val regex = """
        \s*Monkey (\d+):
        \s*Starting items: ((?:\d+)?(?:, \d+)*)
        \s*Operation: new = old (.) (\d+|old)
        \s*Test: divisible by (\d+)
        \s*If true: throw to monkey (\d+)
        \s*If false: throw to monkey (\d+)""".trimIndent().toRegex()
    val targets = mutableMapOf<Int, Pair<Int, Int>>()

    val monkeys = regex.findAll(input.joinToString("\n")).map { result ->
        val values = result.groupValues.drop(1)
        val id = values[0].toInt()
        val items = values[1].split(", ").map(String::toInt).toMutableList()
        val operation = values[2].single()
        val operationValue = values[3].toIntOrNull()
        val divisor = values[4].toInt()
        val divisibleTargetMonkeyIdx = values[5].toInt()
        val indivisibleTargetMonkeyIdx = values[6].toInt()

        targets[id] = Pair(divisibleTargetMonkeyIdx, indivisibleTargetMonkeyIdx)

        Monkey(id, items, operation, operationValue, divisor)
    }.toList()

    monkeys.forEach { monkey ->
        val (divisibleIdx, indivisibleIdx) = targets[monkey.id]!!

        monkey.divisibleTargetMonkey = monkeys[divisibleIdx]
        monkey.indivisibleTargetMonkey = monkeys[indivisibleIdx]
    }

    return monkeys
}

fun tossItems(monkeys: List<Monkey>, count: Int, divisor: Int): Long {
    assert(monkeys.size > 1)

    val lcm = getLCM(*monkeys.map { it.divisor }.toIntArray())

    repeat(count) {
        monkeys.forEach { monkey ->
            monkey.throwItems(lcm, divisor)
        }
    }

    return monkeys
        .map { it.inspections.toLong() }
        .sortedDescending()
        .take(2)
        .reduce(Long::times)
}

fun part1(input: List<String>): Long = tossItems(readMonkeys(input), 20, 3)

fun part2(input: List<String>): Long = tossItems(readMonkeys(input), 10000, 1)

fun main() {
    val testInput = readInput("Day11_test")
    expect(part1(testInput), 10605)
    expect(part2(testInput), 2713310158)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
