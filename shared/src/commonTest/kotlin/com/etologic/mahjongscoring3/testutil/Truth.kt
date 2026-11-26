package com.etologic.mahjongscoring3.testutil

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Tiny subset of Google Truth used by the Mahjong Scoring 2 tests, so they can be ported to commonTest
 * (Truth is JVM only) with their assertions untouched.
 */
fun <T> assertThat(actual: T): Subject<T> = Subject(actual)

class Subject<T>(val actual: T) {
    fun isEqualTo(expected: Any?) = assertEquals(expected, actual as Any?)
    fun isNotEqualTo(unexpected: Any?) = assertTrue(actual != unexpected, "Expected not <$unexpected> but was <$actual>")
    fun isNull() = assertEquals(null, actual as Any?)
    fun isNotNull() = assertTrue(actual != null, "Expected not null")
    fun isTrue() = assertEquals(true, actual as Any?)
    fun isFalse() = assertEquals(false, actual as Any?)
    fun isSameInstanceAs(expected: Any?) = assertTrue(actual === expected, "Expected same instance as <$expected> but was <$actual>")
    fun isInstanceOf(check: (Any?) -> Boolean) = assertTrue(check(actual), "Unexpected type: $actual")

    @Suppress("UNCHECKED_CAST")
    private fun comparable(): Comparable<Any> = actual as Comparable<Any>
    fun isGreaterThan(other: Any) = assertTrue(comparable() > other, "Expected <$actual> > <$other>")
    fun isAtLeast(other: Any) = assertTrue(comparable() >= other, "Expected <$actual> >= <$other>")
    fun isLessThan(other: Any) = assertTrue(comparable() < other, "Expected <$actual> < <$other>")
    fun isAtMost(other: Any) = assertTrue(comparable() <= other, "Expected <$actual> <= <$other>")

    private fun items(): List<Any?> = when (val a = actual as Any?) {
        is Iterable<*> -> a.toList()
        is Array<*> -> a.toList()
        is IntArray -> a.toList()
        is LongArray -> a.toList()
        is Map<*, *> -> a.entries.toList()
        is String -> a.toList()
        else -> fail("Not a collection: $a")
    }

    fun hasSize(size: Int) = assertEquals(size, items().size, "Size of $actual")
    fun isEmpty() = assertTrue(items().isEmpty(), "Expected empty but was $actual")
    fun isNotEmpty() = assertTrue(items().isNotEmpty(), "Expected not empty")
    fun contains(element: Any?) {
        val a = actual as Any?
        if (a is String && element is String) assertTrue(a.contains(element), "Expected <$a> to contain <$element>")
        else assertTrue(items().contains(element), "Expected $actual to contain <$element>")
    }
    fun doesNotContain(element: Any?) {
        val a = actual as Any?
        if (a is String && element is String) assertTrue(!a.contains(element), "Expected <$a> not to contain <$element>")
        else assertTrue(!items().contains(element), "Expected $actual not to contain <$element>")
    }
    fun startsWith(prefix: String) = assertTrue((actual as String).startsWith(prefix), "Expected <$actual> to start with <$prefix>")
    fun endsWith(suffix: String) = assertTrue((actual as String).endsWith(suffix), "Expected <$actual> to end with <$suffix>")
    fun containsExactly(vararg expected: Any?): Ordered = containsExactlyElementsIn(expected.toList())
    fun containsExactlyElementsIn(expected: Iterable<*>): Ordered {
        val actualItems = items()
        val expectedItems = expected.toList()
        assertEquals(expectedItems.groupingBy { it }.eachCount(), actualItems.groupingBy { it }.eachCount(), "Elements of $actualItems vs $expectedItems")
        return Ordered(expectedItems, actualItems)
    }
    fun containsExactlyElementsIn(expected: Array<*>): Ordered = containsExactlyElementsIn(expected.toList())
    fun containsAtLeast(vararg expected: Any?) = assertTrue(items().containsAll(expected.toList()), "Expected $actual to contain $expected")
    fun containsNoneOf(vararg unexpected: Any?) = assertTrue(unexpected.none { it in items() }, "Expected $actual to contain none of ${unexpected.toList()}")
    fun isIn(collection: Iterable<*>) = assertTrue(actual in collection, "Expected <$actual> in $collection")
    fun containsKey(key: Any?) = assertTrue((actual as Map<*, *>).containsKey(key), "Expected key <$key> in $actual")
    fun hasLength(length: Int) = assertEquals(length, (actual as CharSequence).length)
}

class Ordered(private val expected: List<Any?>, private val actual: List<Any?>) {
    fun inOrder() = assertEquals(expected, actual, "Order")
}
