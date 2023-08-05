package com.pranay.pokemon.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun ShowLoaderOrErrorRetry(
    modifier: Modifier = Modifier,
    loading: Boolean,
    errorMessage: String? = null,
    retryAction: () -> Unit
) {
    Box(
        modifier = modifier
            .wrapContentSize()
    ) {
        if (errorMessage != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Button(onClick = { retryAction.invoke() }) {
                    Text(text = "Retry")
                }
            }
        } else
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
    }
}