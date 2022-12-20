package day20

import utils.*

const val KEY = 811589153

class Element<T>(val value: T) {
    private var prev: Element<T> = this
    private var next: Element<T> = this

    private val sequence get() = generateSequence(this) { it.next }

    private val reverseSequence get() = generateSequence(this) { it.prev }

    fun append(element: Element<T>) {
        element.next = next
        element.prev = this

        next.prev = element
        next = element
    }

    private fun disconnect() {
        prev.next = next
        next.prev = prev
    }

    fun moveForward(offset: Int) {
        disconnect()
        prev.sequence.elementAt(offset).append(this)
    }

    fun moveBackward(offset: Int) {
        disconnect()
        prev.reverseSequence.elementAt(offset).append(this)
    }

    companion object {
        fun <T> buildSequence(list: List<T>): Sequence<Element<T>> =
            list
                .drop(1)
                .fold(Element(list.first())) { prev, value ->
                    Element(value).also { prev.append(it) }
                }
                .next
                .sequence
    }
}

fun mix(list: List<Long>, count: Int = 1): List<Long> {
    val size = list.size
    val sequence = Element.buildSequence(list)
    val elements = sequence.take(size).toList()

    repeat(count) {
        elements.forEach { element ->
            val forwardOffset = Math.floorMod(element.value, size - 1)
            val backwardOffset = size - 1 - forwardOffset

            if (forwardOffset < backwardOffset)
                element.moveForward(forwardOffset)
            else
                element.moveBackward(backwardOffset)
        }
    }

    return sequence
        .map { it.value }
        .dropWhile { it != 0L }
        .take(size)
        .toList()
}

fun part1(input: List<String>): Long {
    val numbers = input.map { it.toLong() }
    val mixed = mix(numbers)

    return (1000..3000 step 1000).sumOf { mixed[it % mixed.size] }
}

fun part2(input: List<String>): Long {
    val numbers = input.map { it.toLong() * KEY }
    val mixed = mix(numbers, 10)

    return (1000..3000 step 1000).sumOf { mixed[it % mixed.size] }
}

fun main() {
    val testInput = readInput("Day20_test")
    expect(part1(testInput), 3)
    expect(part2(testInput), 1623178306)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
