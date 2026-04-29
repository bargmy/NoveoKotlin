package ir.hienob.noveo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.zip.GZIPInputStream

private val tgsClient = OkHttpClient()

@Composable
internal fun TgsSticker(
    url: String?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    var json by remember(url) { mutableStateOf<String?>(null) }
    var failed by remember(url) { mutableStateOf(false) }

    LaunchedEffect(url) {
        json = null
        failed = false
        if (url.isNullOrBlank()) {
            failed = true
            return@LaunchedEffect
        }
        runCatching {
            withContext(Dispatchers.IO) {
                val request = Request.Builder().url(url).build()
                tgsClient.newCall(request).execute().use { response ->
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

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        when {
            composition != null -> {
                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier.fillMaxSize()
                )
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
