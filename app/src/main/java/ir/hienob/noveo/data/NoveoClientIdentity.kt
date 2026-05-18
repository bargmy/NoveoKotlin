package ir.hienob.noveo.data

import android.os.Build
import ir.hienob.noveo.BuildConfig
import java.util.Locale
import okhttp3.Request
import org.json.JSONObject

internal object NoveoClientIdentity {
    const val clientId = "noveo-android"
    const val clientName = "Noveo Android"
    private const val appName = "Noveo"

    private val unsafeHeaderChars = Regex("[^ -~]+|[;()\\r\\n]+")
    private val whitespace = Regex("\\s+")

    val version: String
        get() = BuildConfig.VERSION_NAME

    val osVersion: String
        get() = headerPart(Build.VERSION.RELEASE).ifBlank { Build.VERSION.SDK_INT.toString() }

    val deviceModel: String
        get() {
            val manufacturer = headerPart(Build.MANUFACTURER)
            val model = headerPart(Build.MODEL)
            return listOf(manufacturer, model)
                .filter { it.isNotBlank() }
                .distinctBy { it.lowercase(Locale.ROOT) }
                .joinToString(" ")
                .ifBlank { "Android Device" }
        }

    val userAgent: String
        get() = "NoveoAndroid/$version (Android $osVersion; SDK ${Build.VERSION.SDK_INT}; $deviceModel; ${BuildConfig.APPLICATION_ID}) NoveoKotlin/$version"

    val headers: Map<String, String>
        get() = mapOf(
            "User-Agent" to userAgent,
            "X-Noveo-Client" to clientId,
            "X-Noveo-Client-Name" to clientName,
            "X-Noveo-Client-Version" to version,
            "X-Noveo-Version" to version
        )

    fun clientInfoJson(): JSONObject = JSONObject()
        .put("client", clientId)
        .put("clientId", clientId)
        .put("clientName", clientName)
        .put("clientVersion", version)
        .put("appName", appName)
        .put("appVersion", version)
        .put("platform", "android")
        .put("osName", "Android")
        .put("osVersion", osVersion)
        .put("sdkInt", Build.VERSION.SDK_INT)
        .put("deviceModel", deviceModel)
        .put("packageName", BuildConfig.APPLICATION_ID)
        .put("userAgent", userAgent)
        .put("version", version)

    private fun headerPart(value: String?): String = value.orEmpty()
        .replace(unsafeHeaderChars, " ")
        .replace(whitespace, " ")
        .trim()
}

internal fun Request.Builder.noveoClientHeaders(): Request.Builder = apply {
    NoveoClientIdentity.headers.forEach { (name, value) -> header(name, value) }
}
