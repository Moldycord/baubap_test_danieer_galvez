package com.baubaptest.features.onboarding.presentation.views.general

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun Loader(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.onBackground,
    indicatorColor: Color = Color(0xFF9B5DE5) // morado vibrante
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = indicatorColor,
                strokeWidth = 4.dp
            )
        }
    }
}
