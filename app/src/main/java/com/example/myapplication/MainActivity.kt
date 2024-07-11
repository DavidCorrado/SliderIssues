@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.tokens.SliderTokens
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.lerp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        var sliderValue by remember { mutableFloatStateOf(0f) }
                        Text(text = "Hello World")
                        Text(text = "Hello World")
                        Text(text = "Hello World")
                        Text(text = "Hello World")
                        Text(text = "Hello World")
                        Row {
                            WellSlider(
                                value = sliderValue,
                                valueRange = 0f..4f,
                                stepSize = 1f,
                                labelFormatter = listOf("David", "Corrado", "Jessica", "Benson", "Skylar"),
                                onValueChange = {
                                    sliderValue = it
                                }
                            )
                        }
                    }

                }
            }
        }
    }
}

const val WELL_SLIDER_TEST_TAG = "WELL_SLIDER_TEST_TAG"

@ExperimentalMaterial3Api
@Composable
fun WellSlider(
    value: Float,
    labelFormatter: List<String>,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit = {},
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    stepSize: Float = 0f
) {
    val interactionSource = remember { MutableInteractionSource() }
    var selectedLabel by remember {
        mutableStateOf(
            labelFormatter.getOrNull(value.toInt()).orEmpty()
        )
    }
    Text(
        selectedLabel,
        modifier = Modifier.padding(8.dp).clearAndSetSemantics {
            contentDescription = selectedLabel
            liveRegion = LiveRegionMode.Polite }
    )
    val thumb: @Composable (SliderState) -> Unit = {
        Label(
            label = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Column {
                            Text(
                                selectedLabel,
                                modifier = Modifier.padding(8.dp).semantics { liveRegion = LiveRegionMode.Polite }
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .rotate(180f)
                            .size(8.dp),
                        shape = TriangleShape(),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                    }
                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                }
            },
            interactionSource = interactionSource
        ) {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                colors = SliderDefaults.colors(),
                enabled = enabled
            )
        }
    }

    val steps =
        if (stepSize == 0f) {
            0
        } else {
            ((valueRange.endInclusive - valueRange.start - 1) / stepSize).toInt()
        }
    val state = remember(
        steps,
        valueRange
    ) {
        SliderState(
            value,
            if (stepSize == 0f) 0 else steps,
            null,
            valueRange

        )
    }
    state.onValueChange = {
        onValueChange(it)
        selectedLabel = labelFormatter.getOrNull(it.roundToInt()).orEmpty()
    }
    state.value = value
    Slider2(
        state = state,
        modifier = modifier
            .testTag(WELL_SLIDER_TEST_TAG).semantics {
                contentDescription = labelFormatter.getOrNull(value.roundToInt()).orEmpty()
            },
        enabled = enabled,
        thumb = thumb,
        interactionSource = interactionSource
    )
}

class TriangleShape : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density) =
        Outline.Generic(
            Path().apply {
                moveTo(x = size.width / 2, y = 0f)
                lineTo(x = size.width, y = size.height)
                lineTo(x = 0f, y = size.height)
            }
        )
}

@OptIn(ExperimentalMaterial3Api::class)
private fun Modifier.sliderSemantics2(
    state: SliderState,
    enabled: Boolean
): Modifier {
    return semantics {
        if (!enabled) disabled()
        setProgress(
            label = "Label",
            action = { targetValue ->
                var newValue = targetValue.coerceIn(
                    state.valueRange.start,
                    state.valueRange.endInclusive
                )
                val originalVal = newValue
                val resolvedValue = if (state.steps > 0) {
                    var distance: Float = newValue
                    for (i in 0..state.steps + 1) {
                        val stepValue = lerp(
                            state.valueRange.start,
                            state.valueRange.endInclusive,
                            i.toFloat() / (state.steps + 1)
                        )
                        if (abs(stepValue - originalVal) <= distance) {
                            distance = abs(stepValue - originalVal)
                            newValue = stepValue
                        }
                    }
                    newValue
                } else {
                    newValue
                }

                // This is to keep it consistent with AbsSeekbar.java: return false if no
                // change from current.
                if (resolvedValue == state.value) {
                    false
                } else {
                    if (resolvedValue != state.value) {
                        if (state.onValueChange != null) {
                            state.onValueChange?.let {
                                it(resolvedValue)
                            }
                        } else {
                            state.value = resolvedValue
                        }
                    }
                    state.onValueChangeFinished?.invoke()
                    true
                }
            }
        )
    }.progressSemantics(
        state.value,
        state.valueRange.start..state.valueRange.endInclusive,
        state.steps
    ).semantics { contentDescription = "Test" }
}

@Composable
@ExperimentalMaterial3Api
fun Slider2(
    state: SliderState,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SliderColors = SliderDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderState) -> Unit = {
        SliderDefaults.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled
        )
    },
    track: @Composable (SliderState) -> Unit = { sliderState ->
        SliderDefaults.Track(
            colors = colors,
            enabled = enabled,
            sliderState = sliderState
        )
    }
) {
    require(state.steps >= 0) { "steps should be >= 0" }

    SliderImpl2(
        state = state,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SliderImpl2(
    modifier: Modifier,
    state: SliderState,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    thumb: @Composable (SliderState) -> Unit,
    track: @Composable (SliderState) -> Unit
) {
    state.isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val press = Modifier.sliderTapModifier2(
        state,
        interactionSource,
        enabled
    )
    val drag = Modifier.draggable(
        orientation = Orientation.Horizontal,
        reverseDirection = state.isRtl,
        enabled = enabled,
        interactionSource = interactionSource,
        onDragStopped = {  },
        startDragImmediately = state.isDragging,
        state = state
    )

    Layout(
        {
            Box(modifier = Modifier.layoutId(SliderComponents2.THUMB)) {
                thumb(state)
            }
            Box(modifier = Modifier.layoutId(SliderComponents2.TRACK)) {
                track(state)
            }
        },
        modifier = modifier
            .minimumInteractiveComponentSize()
            .requiredSizeIn(
                minWidth = SliderTokens.HandleWidth,
                minHeight = SliderTokens.HandleHeight
            )
            .sliderSemantics2(
                state,
                enabled
            )
            .focusable(enabled, interactionSource)
            .then(press)
            .then(drag)
    ) { measurables, constraints ->

        val thumbPlaceable = measurables.fastFirst {
            it.layoutId == SliderComponents2.THUMB
        }.measure(constraints)

        val trackPlaceable = measurables.fastFirst {
            it.layoutId == SliderComponents2.TRACK
        }.measure(
            constraints.offset(
                horizontal = - thumbPlaceable.width
            ).copy(minHeight = 0)
        )

        val sliderWidth = thumbPlaceable.width + trackPlaceable.width
        val sliderHeight = max(trackPlaceable.height, thumbPlaceable.height)

        state.updateDimensions(
            thumbPlaceable.width.toFloat(),
            sliderWidth
        )

        val trackOffsetX = thumbPlaceable.width / 2
        val thumbOffsetX = ((trackPlaceable.width) * state.coercedValueAsFraction).roundToInt()
        val trackOffsetY = (sliderHeight - trackPlaceable.height) / 2
        val thumbOffsetY = (sliderHeight - thumbPlaceable.height) / 2

        layout(sliderWidth, sliderHeight) {
            trackPlaceable.placeRelative(
                trackOffsetX,
                trackOffsetY
            )
            thumbPlaceable.placeRelative(
                thumbOffsetX,
                thumbOffsetY
            )
        }
    }
}

private enum class SliderComponents2 {
    THUMB,
    TRACK
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
private fun Modifier.sliderTapModifier2(
    state: SliderState,
    interactionSource: MutableInteractionSource,
    enabled: Boolean
) = if (enabled) {
    pointerInput(state, interactionSource) {
        detectTapGestures(
            onPress = { state.onPress(it) },
            onTap = {
                state.dispatchRawDelta(0f)
                //state.gestureEndAction()
            }
        )
    }
} else {
    this
}
