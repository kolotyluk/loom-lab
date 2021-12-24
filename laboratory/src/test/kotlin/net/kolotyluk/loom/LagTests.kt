package net.kolotyluk.loom

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue

import org.junit.jupiter.api.Test
import java.lang.Thread.sleep

import java.time.Duration
import kotlin.concurrent.thread

class LagTests {

    private val minimumDuration = Duration.ofMillis(10)
    private val maximumDuration = Duration.ofMillis(20)

    @Test
    fun durationDefinite() {
        val lag = Lag(minimumDuration);
        val computedDuration = lag.duration
        assertEquals(minimumDuration, computedDuration)
    }

    @Test
    fun durationIndefinite() {
        val lag = Lag(minimumDuration, maximumDuration);
        val computedDuration = lag.duration
        assertNotEquals(minimumDuration, computedDuration)
        assertNotEquals(maximumDuration, computedDuration)
        assertTrue(computedDuration.minus(minimumDuration).isPositive)
        assertTrue(computedDuration.minus(maximumDuration).isNegative)
    }
}