package ir.hienob.noveo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.zip.GZIPInputStream

private var tgsClient: OkHttpClient? = null

internal fun initializeTgsSupport(context: android.content.Context) {
    if (tgsClient == null) {
        tgsClient = OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "tgs_cache"), 100 * 1024 * 1024))
            .build()
    }
    EmojiTgsManager.initialize(context)
}

@Composable
internal fun TgsSticker(
    url: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
    iterations: Int = LottieConstants.IterateForever,
    restartOnPlay: Boolean = false
) {
    var json by remember(url) { mutableStateOf<String?>(null) }
    var failed by remember(url) { mutableStateOf(false) }
    var playCount by remember(url) { mutableStateOf(0) }

    LaunchedEffect(url) {
        json = null
        failed = false
        if (url.isNullOrBlank()) {
            failed = true
            return@LaunchedEffect
        }
        runCatching {
            withContext(Dispatchers.IO) {
                val client = tgsClient ?: OkHttpClient()
                val request = Request.Builder().url(url).build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) error("TGS download failed")
                    val body = response.body ?: error("Empty TGS response")
                    GZIPInputStream(body.byteStream()).bufferedReader().use { it.readText() }
                }
            }
        }.onSuccess {
            json = it
        }.onFailure {
            failed = true
        }
    }

    val composition by rememberLottieComposition(
        spec = json?.let(LottieCompositionSpec::JsonString) ?: LottieCompositionSpec.JsonString("{}")
    )

    Box(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            playCount++
        },
        contentAlignment = Alignment.Center
    ) {
        when {
            composition != null -> {
                LottieAnimation(
                    composition = composition,
                    iterations = if (iterations == 1) 1 else iterations,
                    restartOnPlay = restartOnPlay,
                    isPlaying = true,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Trigger a re-composition/re-play when playCount changes
                if (iterations == 1 && playCount > 0) {
                    key(playCount) {
                        LottieAnimation(
                            composition = composition,
                            iterations = 1,
                            restartOnPlay = true,
                            isPlaying = true,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            failed -> {
                Text(
                    text = "TGS",
                    color = tint,
                    fontWeight = FontWeight.Bold
                )
            }

            else -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.dp,
                    color = tint
                )
            }
        }
    }
}
