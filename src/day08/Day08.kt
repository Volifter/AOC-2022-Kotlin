package day08

import utils.*

data class Tree(val height: Int, var score: Int = 0)

fun readMap(input: List<String>): List<List<Tree>> =
    input.map { line ->
        line.map { c ->
            Tree(c.digitToInt())
        }
    }

fun <T> getClockwiseRotation(mtx: List<List<T>>): List<List<T>> =
    mtx[0].indices.map { x ->
        mtx.indices.map { y ->
            mtx[mtx.lastIndex - y][x]
        }
    }

fun setTreeVisibilityScores(map: List<List<Tree>>) {
    val rotations = (0..2).scan(map) { mtx, _ -> getClockwiseRotation(mtx) }

    rotations.forEach { rows ->
        rows.forEach { row ->
            row.fold(-1) { max, tree ->
                if (tree.height > max)
                    tree.score++

                maxOf(max, tree.height)
            }
        }
    }
}

fun part1(input: List<String>): Int =
    readMap(input)
        .also { setTreeVisibilityScores(it) }
        .flatten()
        .count { tree -> tree.score > 0 }

fun getTreeScenicScore(map: List<List<Tree>>, x: Int, y: Int): Int {
    val currentHeight = map[y][x].height
    val directions = listOf(
        (x + 1..map[0].lastIndex).map { map[y][it].height },
        (x - 1 downTo 0).map { map[y][it].height },
        (y + 1..map.lastIndex).map { map[it][x].height },
        (y - 1 downTo 0).map { map[it][x].height }
    )

    return directions
        .map { dir ->
            (dir.indexOfFirst { it >= currentHeight } + 1)
                .takeIf { it != 0 }
                ?: dir.size
        }
        .reduce(Int::times)
}

fun setTreeScenicScores(map: List<List<Tree>>) =
    map.forEachIndexed { y, row ->
        row.forEachIndexed { x, tree ->
            tree.score = getTreeScenicScore(map, x, y)
        }
    }

fun part2(input: List<String>): Int =
    readMap(input)
        .also { setTreeScenicScores(it) }
        .flatten()
        .maxOf { it.score }

fun main() {
    val testInput = readInput("Day08_test")
    expect(part1(testInput), 21)
    expect(part2(testInput), 8)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
