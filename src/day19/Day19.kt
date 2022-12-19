package day19

import utils.*
import kotlin.math.ceil

val ROBOT_REGEX =
    """Each (\w+) robot costs (\d+) (\w+)(?: and (\d+) (\w+))?.""".toRegex()

val MATERIALS = listOf("ore", "clay", "obsidian", "geode")

data class State(
    val robots: Map<String, Int>,
    val materials: Map<String, Int>,
    val time: Int
) : Comparable<State> {
    fun canDoBetter(maxGeodes: Int) =
        (
            materials["geode"]!!
                + time * (time + 2 * robots["geode"]!! - 1) / 2
        ) > maxGeodes

    fun getSubStates(
        robotsCosts: Map<String, Map<String, Int>>,
        maxCosts: Map<String, Int>
    ) = sequence {
        val buildingStates = robotsCosts.mapNotNull { (material, costs) ->
            if (
                costs.any { (name, cost) ->
                    materials[name]!! < cost && robots[name]!! == 0
                }
                || maxCosts[material]?.let { robots[material]!! == it } == true
            )
                return@mapNotNull null

            val duration = 1 + costs.maxOf { (name, cost) ->
                maxOf(
                    0,
                    ceil(
                        1.0 * (cost - materials[name]!!) / robots[name]!!
                    ).toInt()
                )
            }

            if (duration > time)
                return@mapNotNull null

            return@mapNotNull State(
                robots + mapOf(material to robots[material]!! + 1),
                MATERIALS.associateWith { name ->
                    (
                        materials[name]!!
                            + robots[name]!! * duration
                            - (costs[name] ?: 0)
                    )
                },
                time - duration
            )
        }

        if (buildingStates.isNotEmpty())
            yieldAll(buildingStates)
        else
            yield(State(
                robots,
                MATERIALS.associateWith { name ->
                    materials[name]!! + robots[name]!! * time
                },
                0
            ))
    }

    override fun compareTo(other: State): Int {
        if (time != other.time)
            return other.time - time

        materials.values.zip(other.materials.values).reversed().map { (a, b) ->
            if (a != b)
                return b - a
        }

        robots.values.zip(other.robots.values).reversed().map { (a, b) ->
            if (a != b)
                return b - a
        }

        return 0
    }
}

fun parseBlueprints(
    input: List<String>
): Map<Int, Map<String, Map<String, Int>>> =
    input.associate { line ->
        val (title, contents) = line.split(": ")
        val id = title.substring(10).toInt()
        val matches = ROBOT_REGEX.findAll(contents).map {
            it.groupValues.drop(1).filter { match -> match.isNotEmpty() }
        }
        val materials = matches.associate { parts ->
            val costs = parts
                .drop(1)
                .chunked(2)
                .associate { Pair(it[1], it[0].toInt()) }

            Pair(parts[0], costs)
        }

        id to materials
    }

fun solve(robotsCosts: Map<String, Map<String, Int>>, time: Int): Int {
    val robots = MATERIALS.associateWith { if (it == "ore") 1 else 0 }
    val materials = MATERIALS.associateWith { 0 }
    val maxCosts = MATERIALS
        .take(3)
        .associateWith { material ->
            robotsCosts.values.maxOf { costs -> costs[material] ?: 0 }
        }
    var maxGeodes = 0

    generateSequence(listOf(State(robots, materials, time))) { stack ->
        stack
            .flatMap { it.getSubStates(robotsCosts, maxCosts) }
            .onEach { state ->
                if (state.time == 0)
                    maxGeodes = maxOf(maxGeodes, state.materials["geode"]!!)
            }
            .filter { state ->
                state.time > 0 && state.canDoBetter(maxGeodes)
            }
            .sortedDescending()
    }.find { it.isEmpty() }

    return maxGeodes
}

fun part1(input: List<String>): Int {
    val blueprints = parseBlueprints(input)

    return blueprints.entries.sumOf { (number, blueprint) ->
        number * solve(blueprint, 24)
    }
}

fun part2(input: List<String>): Int {
    val blueprints = parseBlueprints(input)

    return blueprints.values.take(3).fold(1) { acc, blueprint ->
        acc * solve(blueprint, 32)
    }
}

fun main() {
    val testInput = readInput("Day19_test")
    expect(part1(testInput), 33)
    expect(part2(testInput), 3472)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}
