package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.roony11_1.myapp.ui.viewmodel.IUiState

@Composable
fun <T : Any> UiStateContent(
    state: IUiState<T?>,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = { UiLoadingContent() },
    errorContent: @Composable (String) -> Unit = { UiErrorContent(it) },
    emptyContent: @Composable () -> Unit = {},
    content: @Composable (T) -> Unit)
{
    when
    {
        state.isIdle -> Unit

        state.isLoading -> Box(
            modifier = modifier,
            contentAlignment = Alignment.Center)
        {
            loadingContent()
        }

        else -> {
            val error = state.error
            val data = state.data

            when
            {
                error != null -> Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center)
                {
                    errorContent(error)
                }

                data != null -> Box(modifier = modifier)
                {
                    content(data)
                }

                else -> Box(
                    modifier = modifier,
                    contentAlignment = Alignment.Center)
                {
                    emptyContent()
                }
            }
        }
    }
}

@Composable
fun UiLoadingContent(modifier: Modifier = Modifier)
{
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cargando...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun UiErrorContent(
    mensaje: String,
    modifier: Modifier = Modifier)
{
    Surface(
        modifier = modifier.padding(24.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.errorContainer,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.error.copy(alpha = 0.25f)))
    {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top)
        {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Start)
        }
    }
}
