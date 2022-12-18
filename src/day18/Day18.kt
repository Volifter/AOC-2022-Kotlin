package day18

import utils.*

val NEIGHBOR_OFFSETS = listOf(
    Voxel(1, 0, 0),
    Voxel(0, 1, 0),
    Voxel(0, 0, 1),
    Voxel(-1, 0, 0),
    Voxel(0, -1, 0),
    Voxel(0, 0, -1)
)

data class Voxel(val x: Int, val y: Int, val z: Int) {
    val neighbors get() =
        NEIGHBOR_OFFSETS.map { dir -> this + dir }

    operator fun plus(other: Voxel): Voxel =
        Voxel(x + other.x, y + other.y, z + other.z)

    operator fun minus(other: Voxel): Voxel =
        Voxel(x - other.x, y - other.y, z - other.z)

    override operator fun equals(other: Any?): Boolean =
        other is Voxel
            && other.x == x
            && other.y == y
            && other.z == z

    override fun hashCode(): Int = (x * 31 + y) * 31 + z

    fun isInCuboid(start: Voxel, end: Voxel): Boolean =
        x in start.x..end.x
            && y in start.y..end.y
            && z in start.z..end.z

    fun minCoords(other: Voxel): Voxel =
        Voxel(
            minOf(x, other.x),
            minOf(y, other.y),
            minOf(z, other.z)
        )

    fun maxCoords(other: Voxel): Voxel =
        Voxel(
            maxOf(x, other.x),
            maxOf(y, other.y),
            maxOf(z, other.z)
        )
}

fun parseVoxels(input: List<String>): Set<Voxel> =
    input
        .map { line ->
            val (x, y, z) = line.split(",").map(String::toInt)

            Voxel(x, y ,z)
        }
        .toSet()

fun part1(input: List<String>): Int {
    val voxels = parseVoxels(input)

    return voxels.sumOf { voxel -> voxel.neighbors.count { it !in voxels } }
}

fun floodFill(voxels: Set<Voxel>, start: Voxel, end: Voxel): Int {
    val visited = (voxels + start).toMutableSet()
    var count = 0

    generateSequence(setOf(start)) { stack ->
        stack
            .flatMap { voxel -> voxel.neighbors }
            .filter {
                it.isInCuboid(start, end)
                    && it !in visited
            }
            .toSet()
            .onEach { voxel ->
                count += voxel.neighbors.count { it in voxels }
                visited.add(voxel)
            }
    }.find { it.isEmpty() }

    return count
}

fun part2(input: List<String>): Int {
    val voxels = parseVoxels(input)
    val min = voxels.reduce { acc, next -> acc.minCoords(next) }
    val max = voxels.reduce { acc, next -> acc.maxCoords(next) }

    return floodFill(voxels, min - Voxel(1, 1, 1), max + Voxel(1, 1, 1))
}


fun main() {
    val testInput = readInput("Day18_test")
    expect(part1(testInput), 64)
    expect(part2(testInput), 58)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
