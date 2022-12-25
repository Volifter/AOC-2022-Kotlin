package day25

import utils.*

const val OFFSET = 2

const val BASE = "=-012"

fun getLongFromSNAFU(snafu: String): Long =
    if (snafu.length == 1)
        BASE.indexOf(snafu.last()) - OFFSET.toLong()
    else
        BASE.length * getLongFromSNAFU(snafu.substring(0, snafu.lastIndex)) +
            getLongFromSNAFU("" + snafu.last())

fun getSNAFUFromLong(n: Long): String =
    if (n <= OFFSET)
        "" + BASE[((n + OFFSET) % BASE.length).toInt()]
    else
        getSNAFUFromLong((n + OFFSET) / BASE.length) +
            BASE[((n + OFFSET) % BASE.length).toInt()]

fun part1(input: List<String>): String =
    getSNAFUFromLong(input.sumOf { getLongFromSNAFU(it) })

fun main() {
    val testInput = readInput("Day25_test")
    expect(part1(testInput), "2=-1=0")

    val input = readInput("Day25")
    println(part1(input))
}
