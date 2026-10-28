package com.iftikar.outlier

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.iftikar.outlier.core.designsystem.theme.OutlierTheme
import com.iftikar.outlier.feature.shared.SharedViewModel
import com.iftikar.outlier.navigation.OutlierApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    lateinit var sharedViewModel: SharedViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            sharedViewModel = hiltViewModel<SharedViewModel>()
            OutlierTheme {
                OutlierApp(sharedViewModel = sharedViewModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (::sharedViewModel.isInitialized) {
            sharedViewModel.triggerSave()
        } else {
            Log.e("PostMA- State", "onStop: SharedViewModel not available" )
        }
    }
}