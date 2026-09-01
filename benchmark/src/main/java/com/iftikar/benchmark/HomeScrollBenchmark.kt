package com.iftikar.benchmark

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingGfxInfoMetric
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class HomeScrollBenchmark {

    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @OptIn(ExperimentalMetricApi::class)
    @Test
    fun scrollHome() = benchmarkRule.measureRepeated(
        packageName = "com.iftikar.outlier",
        metrics = listOf(
            FrameTimingGfxInfoMetric()
        ),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()

            startActivityAndWait()

            device.wait(
                Until.findObject(By.res("home_posts")),
                5_000
            ) ?: error("home_posts was not found")
        }
    ) {
        val posts = device.findObject(
            By.res("home_posts")
        ) ?: error("home_posts was not found")

        posts.setGestureMargin(
            device.displayWidth / 5
        )

        repeat(15) {
            posts.swipe(
                Direction.UP,
                0.8f
            )
        }
    }
}