package com.iftikar.outlier.feature.post

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.iftikar.outlier.core.designsystem.component.bars.OutlierTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen() {
    Scaffold(
        topBar = {
            OutlierTopAppBar(
                title = {Text("Post")}
            )
        }
    ) { }
}