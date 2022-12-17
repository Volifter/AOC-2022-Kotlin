package day17

import utils.*

const val WIDTH = 7

val ROCKS = listOf(
    Rock(
        Coords(0, 0),
        Coords(1, 0),
        Coords(2, 0),
        Coords(3, 0)
    ),
    Rock(
        Coords(1, 0),
        Coords(0, -1),
        Coords(1, -1),
        Coords(2, -1),
        Coords(1, -2)
    ),
    Rock(
        Coords(0, 0),
        Coords(1, 0),
        Coords(2, 0),
        Coords(2, -1),
        Coords(2, -2)
    ),
    Rock(
        Coords(0, 0),
        Coords(0, -1),
        Coords(0, -2),
        Coords(0, -3)
    ),
    Rock(
        Coords(0, 0),
        Coords(1, 0),
        Coords(0, -1),
        Coords(1, -1)
    )
)

val FILL_DIRECTIONS = listOf(
    Coords(0, 1),
    Coords(-1, 0),
    Coords(1, 0)
)

data class Coords(var x: Int, var y: Int) {
    operator fun plus(other: Coords): Coords = Coords(x + other.x, y + other.y)

    operator fun minus(other: Coords): Coords = Coords(x - other.x, y - other.y)

    override operator fun equals(other: Any?): Boolean =
        other is Coords
            && other.x == x
            && other.y == y

    override fun hashCode(): Int = x * 31 + y

    fun collidesWithMap(map: Set<Coords>): Boolean =
        x !in (0 until WIDTH) || y > 0 || this in map
}

class Rock(private vararg val offsets: Coords) {
    val minY: Int get() = offsets.minOf { it.y }

    operator fun plus(delta: Coords): Rock = Rock(
        *offsets
            .map { it + delta }
            .toTypedArray()
    )

    fun collidesWithMap(map: Set<Coords>): Boolean =
        offsets.any { it.collidesWithMap(map) }

    fun addToMap(map: MutableSet<Coords>) = map.addAll(offsets)

    override fun toString(): String = "Rock(${offsets.toList()})"
}

class InfiniteCollection<T>(private val items: List<T>) {
    var index = 0

    fun next(): T = items[index].also { index = (index + 1) % items.size }
}

data class State(val gaps: Set<Coords>, val windIdx: Int, val rockIdx: Int)

fun dropRock(
    map: MutableSet<Coords>,
    droppedRock: Rock,
    winds: InfiniteCollection<Coords>
): Rock =
    generateSequence(droppedRock) { prevRock ->
        (prevRock + Coords(0, 1))
            .takeUnless { it.collidesWithMap(map) }
            ?.let { rock ->
                (rock + winds.next())
                    .takeUnless { it.collidesWithMap(map) }
                    ?: rock
            }
    }.last()

fun getGaps(map: Set<Coords>, minY: Int): Set<Coords> {
    val visited = (0 until WIDTH).mapNotNull { x ->
        Coords(x, 0).takeUnless {
            Coords(x, minY).collidesWithMap(map)
        }
    }.toMutableSet()

    generateSequence(visited.toList()) { stack ->
        stack
            .flatMap { coords ->
                FILL_DIRECTIONS.mapNotNull { delta ->
                    (coords + delta)
                        .takeUnless { newCoords ->
                            Coords(
                                newCoords.x,
                                newCoords.y + minY
                            ).collidesWithMap(map)
                                || newCoords in visited
                        }
                        ?.also { visited.add(it) }
                }
            }
            .takeIf { it.isNotEmpty() }
    }.count()

    return visited
}

fun solve(input: String, cycleCount: Long): Long {
    val winds = InfiniteCollection(
        input.map { Coords(if (it == '>') 1 else -1, 0) }
    )
    val rocks = InfiniteCollection(ROCKS)
    val map = mutableSetOf<Coords>()
    var minY = 1
    var cyclesRemaining = cycleCount
    val cache = mutableMapOf<State, Pair<Long, Int>>()
    var extraY: Long? = null

    while (cyclesRemaining > 0) {
        val rockIdx = rocks.index
        val windIdx = winds.index
        val rock = dropRock(map, rocks.next() + Coords(2, minY - 5), winds)

        rock.addToMap(map)

        minY = minOf(minY, rock.minY)

        if (extraY == null) {
            val state = State(getGaps(map, minY), windIdx, rockIdx)

            cache[state]?.let { (prevCycle, prevHeight) ->
                val deltaHeight = prevHeight - minY
                val deltaCycles = prevCycle - cyclesRemaining
                val n = cyclesRemaining / deltaCycles - 1

                extraY = deltaHeight.toLong() * n
                cyclesRemaining -= deltaCycles * n
            }

            cache[state] = Pair(cyclesRemaining, minY)
        }

        cyclesRemaining--
    }

    return 1 - minY.toLong() + (extraY ?: 0)
}

fun part1(input: String): Long = solve(input, 2022)

fun part2(input: String): Long = solve(input, 1_000_000_000_000)


fun main() {
    val testInput = readInput("Day17_test")[0]
    expect(part1(testInput), 3068)
    expect(part2(testInput), 1514285714288)

    val input = readInput("Day17")[0]
    println(part1(input))
    println(part2(input))
}
