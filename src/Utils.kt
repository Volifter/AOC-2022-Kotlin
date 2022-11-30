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
