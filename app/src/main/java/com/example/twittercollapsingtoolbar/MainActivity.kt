package com.example.twittercollapsingtoolbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.twittercollapsingtoolbar.ui.theme.Brown
import com.example.twittercollapsingtoolbar.ui.theme.BrownTransparent
import com.example.twittercollapsingtoolbar.ui.theme.Gray
import com.example.twittercollapsingtoolbar.ui.theme.TwitterCollapsingToolbarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TwitterCollapsingToolbarTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TwitterCollapsingToolbar()
                }
            }
        }
    }
}

val headerHeight = 140.dp
val toolbarHeight = 56.dp
val collapseRange = headerHeight - toolbarHeight
val avatarSize = 90.dp
val paddingMedium = 16.dp
val paddingSmall = 4.dp

@Composable
fun TwitterCollapsingToolbar() {
    val scrollState = rememberScrollState()
    val titleHeight = remember { mutableStateOf(0f) }
    val collapseRangePx = with(LocalDensity.current) { collapseRange.toPx() }
    val avatarSizePx = with(LocalDensity.current) { avatarSize.toPx() }
    val profileNameTopPaddingPx = with(LocalDensity.current) { paddingSmall.toPx() }
    val paddingMediumPx = with(LocalDensity.current) { paddingMedium.toPx() }

    val collapseRangeReached = remember {
        derivedStateOf {
            scrollState.value >= (collapseRangePx)
        }
    }

    val avatarZIndex = remember {
        derivedStateOf {
            if (collapseRangeReached.value)
                0f
            else
                2f
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Body(scrollState, titleHeight, Modifier.zIndex(0f))
        Header(scrollState, collapseRangePx, Modifier.zIndex(1f))
        Avatar(scrollState, collapseRangePx, paddingMediumPx, collapseRangeReached, avatarZIndex)
        Toolbar(
            scrollState,
            collapseRangePx,
            titleHeight,
            avatarSizePx,
            profileNameTopPaddingPx,
            collapseRangeReached,
            Modifier.zIndex(3f)
        )
        ToolbarActions(Modifier.zIndex(4f))
    }
}

@Composable
fun Body(scrollState: ScrollState, titleHeight: MutableState<Float>, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color.Black)
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(Modifier.height(headerHeight))
        Text(
            text = stringResource(id = R.string.edit_profile),
            color = Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .border(1.dp, Gray, RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        Text(
            text = stringResource(R.string.profile_name),
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = paddingSmall, start = paddingMedium)
                .onGloballyPositioned {
                    titleHeight.value = it.size.height.toFloat()
                }
        )
        Text(
            text = stringResource(R.string.tag_name),
            style = MaterialTheme.typography.subtitle2.copy(color = Gray),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(top = paddingSmall, start = paddingMedium)
        )
        repeat(5) {
            Text(
                text = stringResource(R.string.lorem_ipsum),
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(vertical = paddingMedium, horizontal = paddingMedium)
            )
        }
    }
}

@Composable
fun Header(scrollState: ScrollState, collapseRangePx: Float, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(headerHeight)
            .graphicsLayer {
                val collapseFraction = (scrollState.value / collapseRangePx).coerceIn(0f, 1f)
                val yTranslation = lerp(
                    0.dp,
                    -(headerHeight - toolbarHeight),
                    collapseFraction
                )
                translationY = yTranslation.toPx()

                val blur = lerp(0.dp, 3.dp, collapseFraction)
                if (blur != 0.dp) {
                    renderEffect = BlurEffect(blur.toPx(), blur.toPx(), TileMode.Decal)
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.wallpaper),
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun Avatar(
    scrollState: ScrollState,
    collapseRangePx: Float,
    paddingPx: Float,
    switch: State<Boolean>,
    avatarZIndex: State<Float>
) {
    Box(
        modifier = Modifier
            .zIndex(avatarZIndex.value)
            .graphicsLayer {
                val collapseFraction = (scrollState.value / collapseRangePx).coerceIn(0f, 1f)

                val scaleXY = lerp(
                    1.dp,
                    0.5.dp,
                    collapseFraction
                )

                val yTranslation = lerp(
                    headerHeight - (avatarSize / 2),
                    toolbarHeight - (avatarSize * ((1.dp - scaleXY) / 2.dp)),
                    collapseFraction
                )

                translationY = if (switch.value)
                    (toolbarHeight.toPx() - (avatarSize.toPx() * ((1.dp - scaleXY) / 2.dp)) -
                        (scrollState.value - collapseRange.toPx()))
                else
                    yTranslation.toPx()

                translationX = paddingPx

                scaleX = scaleXY.value
                scaleY = scaleXY.value
            }

    ) {
        Image(
            painter = painterResource(id = R.drawable.morad_photo),
            contentDescription = "",
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(paddingSmall, Color.Black, CircleShape)
        )
    }
}


@Composable
fun Toolbar(
    scrollState: ScrollState,
    collapseRangePx: Float,
    titleHeight: MutableState<Float>,
    avatarSizePx: Float,
    profileNameTopPaddingPx: Float,
    showToolbar: State<Boolean>,
    modifier: Modifier
) {
    val showTitle by remember {
        derivedStateOf {
            scrollState.value >= 
                collapseRangePx + avatarSizePx / 2 + profileNameTopPaddingPx + titleHeight.value
        }
    }

    val title = buildAnnotatedString {
        withStyle(style = SpanStyle(fontSize = 20.sp, fontWeight = FontWeight.W700)) {
            append(stringResource(id = R.string.profile_name))
        }
        append("\n")
        withStyle(style = SpanStyle(fontSize = 16.sp, fontWeight = FontWeight.W400)) {
            append(stringResource(id = R.string.tweets))
        }
    }

    AnimatedVisibility(
        visible = showToolbar.value,
        enter = fadeIn(animationSpec = tween(600)),
        exit = fadeOut(animationSpec = tween(600)),
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier.background(
                color = Brown
            ),
            navigationIcon = {},
            title = {
                if (showTitle) {
                    Text(text = title)
                }
            },
            backgroundColor = Color.Transparent,
            elevation = 0.dp
        )
    }
}


@Composable
fun ToolbarActions(modifier: Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(toolbarHeight)
    ) {
        IconAction(
            Modifier.padding(top = paddingMedium, start = paddingMedium),
            Icons.Default.ArrowBack
        )
        Spacer(modifier = Modifier.weight(1f))
        IconAction(
            Modifier.padding(top = paddingMedium, end = paddingMedium),
            Icons.Default.MoreVert
        )
    }
}

@Composable
private fun IconAction(modifier: Modifier, image: ImageVector) {
    IconButton(
        onClick = {},
        modifier = modifier
            .clip(CircleShape)
            .size(32.dp)
            .background(BrownTransparent)
    ) {
        Icon(
            imageVector = image,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier.padding(paddingSmall)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TwitterCollapsingToolbarTheme {
        TwitterCollapsingToolbar()
    }
}