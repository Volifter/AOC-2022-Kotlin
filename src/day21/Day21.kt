package day21

import utils.*

class Monkey {
    var cachedNumber: Long? = null

    var leftMonkey: Monkey? = null
    var rightMonkey: Monkey? = null
    var operator: Char? = null

    val number: Long? get() {
        cachedNumber?.let {
            return it
        }

        if (leftMonkey == null || rightMonkey == null)
            return null

        val (left, right) = Pair(leftMonkey!!.number, rightMonkey!!.number)

        if (left == null || right == null)
            return null

        return when (operator) {
            '+' -> left + right
            '-' -> left - right
            '*' -> left * right
            '/' -> left / right
            else -> throw RuntimeException("invalid operator $operator")
        }.also { cachedNumber = it }
    }

    fun getHumanNumber(expected: Long): Long {
        number?.let {
            return it
        }

        if (operator == null)
            return expected

        val (left, right) = Pair(leftMonkey!!.number, rightMonkey!!.number)

        if (right != null)
            return when (operator) {
                '+' -> expected - right
                '-' -> expected + right
                '*' -> expected / right
                '/' -> expected * right
                else -> throw RuntimeException("invalid operator $operator")
            }.let { leftMonkey!!.getHumanNumber(it) }

        if (left != null)
            return when (operator) {
                '+' -> expected - left
                '-' -> left - expected
                '*' -> expected / left
                '/' -> expected / left
                else -> throw RuntimeException("invalid operator $operator")
            }.let { rightMonkey!!.getHumanNumber(it) }

        throw RuntimeException("loop detected")
    }
}

fun parseMonkeys(input: List<String>): Map<String, Monkey> {
    val descriptions = input.associate { line ->
        line.split(": ").zipWithNext().single()
    }

    return descriptions.keys
        .associateWith { Monkey() }
        .also { monkeys ->
            monkeys.forEach { (name, monkey) ->
                val parts = descriptions[name]!!.split(" ")

                when (parts.size) {
                    1 -> {
                        monkey.cachedNumber = parts[0].toLong()
                    }
                    3 -> {
                        monkey.leftMonkey = monkeys[parts[0]]!!
                        monkey.rightMonkey = monkeys[parts[2]]!!
                        monkey.operator = parts[1].single()
                    }
                    else -> throw RuntimeException(
                        "invalid monkey description format"
                    )
                }
            }
        }
}

fun part1(input: List<String>): Long =
    parseMonkeys(input)["root"]!!.number!!

fun part2(input: List<String>): Long {
    val monkeys = parseMonkeys(input)
    val root = monkeys["root"]!!
    val human = monkeys["humn"]!!
    val expected = root.rightMonkey?.number!!

    human.cachedNumber = null

    return root.leftMonkey?.getHumanNumber(expected)!!
}

fun main() {
    val testInput = readInput("Day21_test")
    expect(part1(testInput), 152)
    expect(part2(testInput), 301)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
