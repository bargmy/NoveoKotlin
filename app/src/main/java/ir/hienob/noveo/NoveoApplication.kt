package ir.hienob.noveo

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import android.os.Build.VERSION.SDK_INT
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

import coil3.PlatformContext

class NoveoApplication : Application(), SingletonImageLoader.Factory {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory())
                add(GifDecoder.Factory())
            }
            .build()
    }
}
