package com.jiangtj.micro.common.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ObjectUtilsTest {

    private data class MutablePayload(
        var nullableText: String?,
        var requiredText: String,
        var count: Int,
        val readOnlyText: String
    )

    @Test
    fun trimAllStringProperties_shouldTrimStringValues() {
        val payload = MutablePayload(
            nullableText = "  hello  ",
            requiredText = "  world\t",
            count = 1,
            readOnlyText = "  keep  "
        )

        payload.trimAllStringProperties()

        assertEquals("hello", payload.nullableText)
        assertEquals("world", payload.requiredText)
    }

    @Test
    fun trimAllStringProperties_shouldConvertNullableBlankToNull() {
        val payload = MutablePayload(
            nullableText = "   ",
            requiredText = "value",
            count = 1,
            readOnlyText = "fixed"
        )

        payload.trimAllStringProperties()

        assertNull(payload.nullableText)
    }

    @Test
    fun trimAllStringProperties_shouldKeepNonNullableBlankAsEmptyString() {
        val payload = MutablePayload(
            nullableText = "value",
            requiredText = "   ",
            count = 1,
            readOnlyText = "fixed"
        )

        payload.trimAllStringProperties()

        assertEquals("", payload.requiredText)
    }

    @Test
    fun trimAllStringProperties_shouldNotAffectNonStringOrReadOnlyProperties() {
        val payload = MutablePayload(
            nullableText = "test",
            requiredText = "value",
            count = 42,
            readOnlyText = "  readOnly  "
        )

        payload.trimAllStringProperties()

        assertEquals(42, payload.count)
        assertEquals("  readOnly  ", payload.readOnlyText)
        assertFalse(payload.readOnlyText == payload.readOnlyText.trim())
    }
}

