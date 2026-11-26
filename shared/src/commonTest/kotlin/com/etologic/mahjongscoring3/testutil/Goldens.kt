package com.etologic.mahjongscoring3.testutil

/**
 * Golden files shared with Mahjong Scoring 2 (copied from its `app/src/test/resources/golden`).
 * MS3 must produce exactly the same output, so these tests only compare: they never rewrite a golden.
 */
expect object GoldenFiles {
    /** Text of `shared/src/commonTest/resources/golden/<relativePath>`, or null if it does not exist. */
    fun readOrNull(relativePath: String): String?
}

object Goldens {

    fun read(relativePath: String): String =
        (GoldenFiles.readOrNull(relativePath) ?: throw AssertionError("Golden file not found: $relativePath"))
            .replace("\r\n", "\n")

    fun assertMatches(relativePath: String, actual: String) {
        val expected = read(relativePath)
        val normalizedActual = actual.replace("\r\n", "\n")
        if (expected != normalizedActual) {
            throw AssertionError(buildDiffMessage(relativePath, expected, normalizedActual))
        }
    }

    private fun buildDiffMessage(relativePath: String, expected: String, actual: String): String {
        val expectedLines = expected.lines()
        val actualLines = actual.lines()
        val firstDifferentLine = (0 until maxOf(expectedLines.size, actualLines.size))
            .first { expectedLines.getOrNull(it) != actualLines.getOrNull(it) }
        return buildString {
            appendLine("Output differs from MS2 golden file $relativePath")
            appendLine("First difference at line ${firstDifferentLine + 1}:")
            appendLine("  expected (MS2): ${expectedLines.getOrNull(firstDifferentLine)}")
            appendLine("  actual (MS3):   ${actualLines.getOrNull(firstDifferentLine)}")
        }
    }
}
