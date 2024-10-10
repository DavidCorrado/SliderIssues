package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    val viewModel = MainViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            PlainSliderPage(uiState.value, {
                viewModel.updateSliderValue(it)
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainSliderPage(
    sliderContent: SliderContent,
    onSliderValueChange: (index: Int) -> Unit,
) {
    MaterialTheme(
        colorScheme =
            MaterialTheme.colorScheme.copy(
                primary = Color.Red,
            ),
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .padding(20.dp),
            ) {
                Spacer(modifier = Modifier.size(80.dp))
                Column {
                    MaterialTheme(
                        colorScheme =
                            MaterialTheme.colorScheme.copy(
                                primary = Color.Red,
                            ),
                    ) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val thumb: @Composable (SliderState) -> Unit = {
                            Label(
                                label = {
                                    Surface(
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.inverseSurface,
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier =
                                                Modifier
                                                    .sizeIn(
                                                        minWidth = 48.dp,
                                                        minHeight = 44.dp,
                                                    ).padding(12.dp),
                                        ) {
                                            Text(
                                                sliderContent.value.toString(),
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                        }
                                    }
                                },
                                interactionSource = interactionSource,
                            ) {
                                SliderDefaults.Thumb(
                                    interactionSource = interactionSource,
                                    colors = SliderDefaults.colors(),
                                )
                            }
                        }
                        Slider(
                            value = sliderContent.value.toFloat(),
                            valueRange = 0f..4f,
                            onValueChange = {
                                onSliderValueChange(it.toInt())
                            },
                            interactionSource = interactionSource,
                            steps = 3,
                            thumb = thumb,
                        )
                    }
                }
            }
        }
    }
}
