package com.iftikar.outlier.feature.search.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.iftikar.outlier.core.designsystem.component.bars.OutlierTopAppBar
import com.iftikar.outlier.core.designsystem.component.input.TextFieldComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onDone: () -> Unit,
    shouldShowBack: Boolean,
    onBackClick: () -> Unit
) {
    OutlierTopAppBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            SearchBarInputItem(
                modifier = Modifier.fillMaxWidth(),
                searchQuery = query,
                onQueryChange = onQueryChange,
                onDone = onDone
            )
        },
        navigationIcon = if (shouldShowBack) {
            {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        } else {
            {}
        }
    )
}

@Composable
private fun SearchBarInputItem(
    modifier: Modifier = Modifier,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onDone: () -> Unit
) {
    TextFieldComponent(
        modifier = modifier.fillMaxWidth(),
        value = searchQuery,
        needToOverwriteColors = true,
        overwriteColors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            focusedSupportingTextColor = Color.White,
            unfocusedSupportingTextColor = Color.White
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search for anything"
            )
        },
        placeholder = {
            Text("Search for anything", style = MaterialTheme.typography.bodyMedium)
        },
        onValueChange = onQueryChange,
        shape = RoundedCornerShape(40.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onDone() }
        )
    )
}

























