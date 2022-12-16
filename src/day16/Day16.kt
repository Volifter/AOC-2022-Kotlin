package day16

import utils.*

val LINE_REGEX = (
    """Valve (\w+) has flow rate=(\d+); """ +
    """tunnels? leads? to valves? (.*)"""
).toRegex()

data class Valve(val name: String, val rate: Int) {
    lateinit var links: List<Valve>

    lateinit var openingCosts: Map<Valve, Int>

    fun computeOpeningCosts() {
        val result = mutableMapOf<Valve, Int>()
        val visited = mutableSetOf<Valve>()
        var openingCost = 1

        generateSequence(listOf(this)) { stack ->
            stack
                .onEach {
                    if (it.rate > 0)
                        result[it] = openingCost
                }
                .flatMap { valve -> valve.links }
                .filter { it !in visited }
                .also {
                    visited.addAll(it)
                    openingCost++
                }
        }.find { it.isEmpty() }

        openingCosts = result
    }
}

data class State(
    val currentValve: Valve,
    val time: Int,
    val openValves: Set<Valve> = setOf(),
    val score: Int = 0
) {
    val subStates get() =
        currentValve.openingCosts
            .filter { (valve, length) -> valve !in openValves && length < time }
            .map { (valve, openingTime) ->
                State(
                    valve,
                    time - openingTime,
                    openValves + valve,
                    score + valve.rate * (time - openingTime)
                )
            }
}

fun parseRootValve(input: List<String>): Valve {
    val valveChildren = mutableMapOf<String, List<String>>()
    val valves = input.associate { line ->
        val (name, rate, children) = LINE_REGEX.find(line)!!.groupValues.drop(1)

        valveChildren[name] = children.split(", ")

        name to Valve(name, rate.toInt())
    }

    valves.entries.forEach { (name, valve) ->
        valve.links = valveChildren[name]!!.map { valves[it]!! }
    }

    valves.values.forEach { it.computeOpeningCosts() }

    return valves["AA"]!!
}

fun computePaths(
    root: Valve,
    totalTime: Int,
    openValves: Set<Valve> = setOf()
): List<Pair<Set<Valve>, Int>> {
    val rootState = State(root, totalTime, openValves)
    val visited = mutableSetOf(rootState)
    val result = mutableMapOf<Set<Valve>, Int>()

    generateSequence(listOf(rootState)) { stack ->
        stack
            .flatMap { state ->
                if (result[state.openValves]?.let { state.score > it } != false)
                    result[state.openValves] = state.score

                state.subStates
                    .filter { it !in visited }
                    .also { visited.addAll(it) }
            }
    }.find { it.isEmpty() }

    return result.map { it.toPair() }
}

fun part1(input: List<String>): Int {
    val root = parseRootValve(input)
    val paths = computePaths(root, 30)

    return paths.maxOf { it.second }
}

fun part2(input: List<String>): Int {
    val root = parseRootValve(input)
    val paths = computePaths(root, 26)

    return paths.maxOf { (openValves, baseScore) ->
        val otherScore = computePaths(root, 26, openValves).maxOf { it.second }

        baseScore + otherScore
    }
}

fun main() {
    val testInput = readInput("Day16_test")
    expect(part1(testInput), 1651)
    expect(part2(testInput), 1707)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}
