package day13

import utils.*

class Packet(private val items: List<Any>) : Comparable<Packet> {
    constructor(str: String) : this(
        splitList(str.substring(1 until str.lastIndex))
            .filter { it.isNotEmpty() }
            .map { it.toIntOrNull() ?: Packet(it) }
    )

    override fun compareTo(other: Packet): Int =
        (items + null)
            .zip(other.items + null)
            .asSequence()
            .map { (left, right) ->
                when {
                    (left is Int && right is Int) -> left - right
                    (left is Packet && right is Packet) -> left.compareTo(right)
                    (left is Packet && right is Int) -> left.compareTo(
                        Packet(listOf(right))
                    )
                    (left is Int && right is Packet) -> -right.compareTo(
                        Packet(listOf(left))
                    )
                    (left == null && right == null) -> 0
                    (left == null) -> -1
                    (right == null) -> 1
                    else -> 0
                }
            }
            .find { it != 0 } ?: 0

    companion object {
        private fun splitList(str: String): List<String> =
            str.fold(Pair(listOf(""), 0)) { (lists, depth), c ->
                val groups = (
                    if (c == ',' && depth == 0)
                        lists + ""
                    else
                        lists.dropLast(1) + (lists.last() + c)
                )
                val deltaDepth = when (c) {
                    '[' -> 1
                    ']' -> -1
                    else -> 0
                }

                Pair(groups, depth + deltaDepth)
            }.first
    }
}

fun readPackets(input: List<String>): List<Packet> =
    input.mapNotNull { line ->
        line
            .takeIf(String::isNotEmpty)
            ?.let(::Packet)
    }

fun part1(input: List<String>): Int =
    readPackets(input)
        .chunked(2)
        .withIndex()
        .filter { (_, pair) -> pair[0] <= pair[1] }
        .sumOf { (i, _) -> i + 1 }

fun part2(input: List<String>): Int {
    val packets = readPackets(input)
    val packetA = Packet("[[2]]")
    val packetB = Packet("[[6]]")

    return (packets.count { it < packetA } + 1) *
        (packets.count { it < packetB } + 2)
}

fun main() {
    val testInput = readInput("Day13_test")
    expect(part1(testInput), 13)
    expect(part2(testInput), 140)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
