package com.etologic.mahjongscoring3.testutil

import java.io.File

actual object GoldenFiles {
    // Android unit tests run with the module directory (shared/) as working directory
    private val rootDir: File =
        File(System.getProperty("goldens.dir") ?: "src/commonTest/resources/golden")

    actual fun readOrNull(relativePath: String): String? =
        File(rootDir, relativePath).takeIf { it.exists() }?.readText()
}
