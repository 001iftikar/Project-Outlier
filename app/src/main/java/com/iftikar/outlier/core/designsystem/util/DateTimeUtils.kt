package com.iftikar.outlier.core.designsystem.util

import java.time.Duration
import java.time.Instant

fun String.toTimeAgo(): String {
    val createdAt = try {
        Instant.parse(this)
    } catch (e: Exception) {
        return ""
    }

    val seconds = Duration.between(
        createdAt,
        Instant.now()
    ).seconds.coerceAtLeast(0)

    return when {
        seconds < 5 -> "now"
        seconds < 60 -> "${seconds}s"

        seconds < 60 * 60 ->
            "${seconds / 60}m"

        seconds < 24 * 60 * 60 ->
            "${seconds / 3600}h"

        seconds < 7 * 24 * 60 * 60 ->
            "${seconds / 86400}d"

        seconds < 30 * 24 * 60 * 60 ->
            "${seconds / (7 * 86400)}w"

        seconds < 365 * 24 * 60 * 60 ->
            "${seconds / (30 * 86400)}mo"

        else ->
            "${seconds / (365 * 86400)}y"
    }
}