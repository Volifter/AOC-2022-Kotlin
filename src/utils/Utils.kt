package utils

import java.io.File

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("inputs", "$name.txt").readLines()

/**
 * A verbose encapsulation of check()
 */
fun <T> expect(got: T, expected: T) {
    try {
        check(got == expected)
    } catch (exception: IllegalStateException) {
        System.err.println("Assertion failed: expected $expected, got $got")

        throw exception
    }

    println("Assertion passed: $got == $got")
}

/**
 * Computes the GCD of its arguments
 */
fun getGCD(vararg values: Int): Int =
    values.toSet().fold(1) { left, right ->
        var a = left
        var b = right

        while (a != b) {
            if (a > b)
                a -= b
            else
                b -= a
        }

        return@fold a
    }

/**
 * Computes the LCM of its arguments
 */
fun getLCM(vararg values: Int): Int = values.toSet().fold(1) { a, b ->
    maxOf(a, b) / getGCD(a, b) * minOf(a, b)
}
