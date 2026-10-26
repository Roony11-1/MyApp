package io.github.roony11_1.myapp.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center)
    {
        CircularProgressIndicator()
    }
}

@Composable
fun UiErrorContent(
    mensaje: String,
    modifier: Modifier = Modifier)
{
    Text(
        text = mensaje,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(24.dp))
}
