package day22

import utils.*

val INSTRUCTION_REGEX = """(\d+|[LR])""".toRegex()

val DIRECTIONS = listOf(
    Coords(1, 0),
    Coords(0, 1),
    Coords(-1, 0),
    Coords(0, -1)
)

val DIRECTIONS_IDX =
    DIRECTIONS
        .withIndex()
        .associate { (i, dir) -> dir to i }

val RELATIVE_OFFSETS = mapOf(
    // Forward face
    "F" to Pair(0, 0),

    // Left face
    "FL" to Pair(-1, -1),
    "FRRR" to Pair(-1, -1),
    "FFL" to Pair(-1, 2),
    "FRFRF" to Pair(-1, 0),
    "FRRFR" to Pair(-1, 0),
    "FFFL" to Pair(-1, 1),
    "FRFF" to Pair(-1, 1),

    // Backward face
    "FFF" to Pair(2, 0),
    "FRR" to Pair(2, 2),
    "FLL" to Pair(2, 2),
    "FRFR" to Pair(2, -1),
    "FFRF" to Pair(2, -1),
    "FLFL" to Pair(2, 1),
    "FFLF" to Pair(2, 1),
    "FRFFR" to Pair(2, 0),
    "FFF" to Pair(2, 0),

    // Right face
    "FR" to Pair(1, 1),
    "FLLL" to Pair(1, 1),
    "FFR" to Pair(1, 2),
    "FLFLF" to Pair(1, 0),
    "FLLFL" to Pair(1, 0),
    "FFFR" to Pair(1, -1),
    "FLFF" to Pair(1, -1)
)

data class Coords(var x: Int, var y: Int) {
    fun wrapIn(size: Coords): Coords =
        Coords(
            Math.floorMod(x, size.x),
            Math.floorMod(y, size.y)
        )

    fun rotate(rotation: Int): Coords {
        return when (Math.floorMod(rotation, 4)) {
            0 -> Coords(x, y)
            1 -> Coords(y, -x - 1)
            3 -> Coords(-y - 1, x)
            2 -> Coords(-x - 1, -y - 1)
            else -> throw IllegalStateException("Math.floorMod() failed")
        }
    }

    operator fun plus(other: Coords): Coords = Coords(x + other.x, y + other.y)

    operator fun times(ratio: Int): Coords = Coords(x * ratio, y * ratio)

    operator fun contains(other: Coords): Boolean =
        other.x in 0 until x
            && other.y in 0 until y

    override operator fun equals(other: Any?): Boolean =
        other is Coords
            && other.x == x
            && other.y == y

    override fun hashCode(): Int = x * 31 + y
}

open class Board(
    protected val faces: Map<Coords, List<String>>,
    val size: Int
) {
    protected open var neighbors: Map<Coords, Map<Coords, Pair<Coords, Int>>> =
        faces.keys.associateWith { face ->
            DIRECTIONS.associateWith { direction ->
                Pair(
                    generateSequence(face + direction) { it + direction }
                        .map { it.wrapIn(Coords(size, size)) }
                        .first { faces[it] != null },
                    0
                )
            }
        }

    private val startingFace get() = faces.keys.minWith { a, b ->
        if (a.y != b.y) a.y - b.y else a.x - b.x
    }

    private fun getCellAt(face: Coords, coords: Coords): Char =
        faces[face]!![coords.y][coords.x]

    private fun getNextPosition(
        face: Coords,
        position: Coords,
        direction: Int
    ): Pair<Pair<Coords, Coords>, Int> {
        val delta = DIRECTIONS[direction]
        var newPosition = position + delta
        var newFace = face
        var newDirection = direction

        if (newPosition !in Coords(size, size)) {
            val (neighborFace, rotation) = neighbors[face]!![delta]!!

            newFace = neighborFace
            newPosition = newPosition
                .rotate(rotation)
                .wrapIn(Coords(size, size))
            newDirection = (direction - rotation + 4) % 4
        }

        if (getCellAt(newFace, newPosition) == '#')
            return Pair(Pair(face, position), direction)

        return Pair(Pair(newFace, newPosition), newDirection)
    }

    fun solve(instructions: List<String>): Pair<Coords, Int> {
        var face = startingFace
        var position = Coords(0, 0)
        var direction = 0

        instructions.forEach { instruction ->
            when (instruction) {
                "R" -> {
                    direction = (direction + 1) % 4
                }
                "L" -> {
                    direction = (direction - 1 + 4) % 4
                }
                else -> {
                    repeat(instruction.toInt()) {
                        val (newPositions, newDirection) = getNextPosition(
                            face,
                            position,
                            direction
                        )

                        face = newPositions.first
                        position = newPositions.second
                        direction = newDirection
                    }
                }
            }
        }

        return Pair(face * size + position, direction)
    }
}

class CubeBoard(
    faces: Map<Coords, List<String>>,
    size: Int
) : Board(faces, size) {
    override var neighbors: Map<Coords, Map<Coords, Pair<Coords, Int>>> =
        faces.keys.associateWith { face ->
            DIRECTIONS
                .map { direction -> findRelativeOffsets(face, direction) }
                .reduce { a, b -> a + b }
        }

    private fun findRelativeOffsets(
        face: Coords,
        dir: Coords
    ): Map<Coords, Pair<Coords, Int>> {
        if (face + dir !in faces)
            return mapOf()

        val relativeDirections = listOf(
            Pair(dir, 'F'),
            Pair(Coords(dir.y, -dir.x), 'L'),
            Pair(Coords(-dir.y, dir.x), 'R')
        )
        val offsets = mutableMapOf<Coords, Pair<Coords, Int>>()
        val visited = mutableSetOf<Coords>()

        generateSequence(listOf(Pair(face + dir, "F"))) { stack ->
            stack.flatMap { (position, path) ->
                RELATIVE_OFFSETS[path]?.let { (directionOffset, rotation) ->
                    val offset = DIRECTIONS[
                        (DIRECTIONS_IDX[dir]!! + directionOffset + 4) % 4
                    ]

                    offsets[offset] = Pair(position, rotation)
                }

                relativeDirections.mapNotNull { (offset, c) ->
                    Pair(position + offset, path + c)
                        .takeIf { (pos, _) -> pos in faces && pos !in visited }
                        ?.also { visited.add(it.first) }
                }
            }
        }.first { it.isEmpty() }

        return offsets
    }
}

fun parseFaces(input: List<String>, size: Int): Map<Coords, List<String>> {
    val maxLineLength = input.maxOf { it.length }
    val faces = mutableMapOf<Coords, List<String>>()

    repeat(input.size / size) { y ->
        repeat(maxLineLength / size) { x ->
            if (
                input
                    .getOrNull(y * size)
                    ?.getOrNull(x * size)
                    ?.takeIf { it != ' ' } != null
            )
                faces[Coords(x, y)] = input
                    .slice(y * size until (y + 1) * size)
                    .map { line -> line.slice(x * size until (x + 1) * size) }
        }
    }

    return faces
}

fun parseInstructions(input: String): List<String> =
    INSTRUCTION_REGEX
        .findAll(input)
        .map { it.value }
        .toList()

fun solve(board: Board, instructions: List<String>): Int {
    val (position, direction) = board.solve(instructions)

    return 1000 * (position.y + 1) + 4 * (position.x + 1) + direction
}

fun part1(input: List<String>, size: Int): Int =
    solve(
        Board(
            parseFaces(input.dropLast(2), size),
            size
        ),
        parseInstructions(input.last())
    )

fun part2(input: List<String>, size: Int): Int =
    solve(
        CubeBoard(
            parseFaces(input.dropLast(2), size),
            size
        ),
        parseInstructions(input.last())
    )

fun main() {
    val testInput = readInput("Day22_test")
    expect(part1(testInput, 4), 6032)
    expect(part2(testInput, 4), 5031)

    val input = readInput("Day22")
    println(part1(input, 50))
    println(part2(input, 50))
}
