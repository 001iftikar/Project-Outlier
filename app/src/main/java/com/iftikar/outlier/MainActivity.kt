package com.iftikar.outlier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iftikar.outlier.core.datastore.SessionViewModel
import com.iftikar.outlier.core.designsystem.theme.OutlierTheme
import com.iftikar.outlier.navigation.OutlierApp
import dagger.hilt.android.AndroidEntryPoint
import io.appwrite.exceptions.AppwriteException

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