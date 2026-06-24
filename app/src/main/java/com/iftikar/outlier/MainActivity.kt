package com.iftikar.outlier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.iftikar.outlier.core.designsystem.theme.OutlierTheme
import com.iftikar.outlier.navigation.OutlierApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OutlierTheme {
                OutlierApp()
            }
        }
    }
}