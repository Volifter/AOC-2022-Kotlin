package day07

import utils.*

open class Entity(open val size: Int = 0)

class File(size: Int) : Entity(size)

class Directory(val parent: Directory? = null) : Entity() {
    val contents: MutableMap<String, Entity> = mutableMapOf()

    private var _size: Int? = null

    override val size: Int get() = _size
        ?: contents.values
            .sumOf { it.size }
            .also { _size = it }

    val subDirs: Sequence<Directory> get() = sequence {
        yield(this@Directory)

        contents.values
            .filterIsInstance<Directory>()
            .forEach { yieldAll(it.subDirs) }
    }
}

fun readCommands(lines: List<String>): Directory {
    val root = Directory()
    var dir = root

    lines.forEach { line ->
        val parts = line.split(" ")

        if (parts[0] == "$" && parts[1] == "cd") {
            dir = when (parts[2]) {
                "/" -> root
                ".." -> dir.parent!!
                else -> dir.contents[parts[2]]!! as Directory
            }
        }

        if (parts[0] != "$") {
            val name = parts[1]

            if (name !in dir.contents)
                dir.contents[name] = if (parts[0] == "dir")
                    Directory(dir)
                else
                    File(parts[0].toInt())
        }
    }

    return root
}

fun part1(input: List<String>, maxSize: Int = 100000): Int =
    readCommands(input).subDirs
        .filter { it.size <= maxSize }
        .sumOf { it.size }

fun part2(input: List<String>, neededSpace: Int = 40000000): Int {
    val root = readCommands(input)
    val target = maxOf(0, root.size - neededSpace)

    return root.subDirs
        .filter { it.size >= target }
        .minOf { it.size }
}

fun main() {
    val testInput = readInput("Day07_test")
    expect(part1(testInput), 95437)
    expect(part2(testInput), 24933642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
