package com.etologic.mahjongscoring3.testutil

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile
import platform.posix.getenv

actual object GoldenFiles {
    // The simulator test task passes the absolute path as SIMCTL_CHILD_GOLDENS_DIR (see shared/build.gradle.kts)
    @OptIn(ExperimentalForeignApi::class)
    private val rootDir: String =
        getenv("GOLDENS_DIR")?.toKString() ?: error("GOLDENS_DIR not set. Run the tests through Gradle.")

    @OptIn(ExperimentalForeignApi::class)
    actual fun readOrNull(relativePath: String): String? =
        NSString.stringWithContentsOfFile("$rootDir/$relativePath", NSUTF8StringEncoding, null)
}
