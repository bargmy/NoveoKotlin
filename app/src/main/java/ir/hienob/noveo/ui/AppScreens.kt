package ir.hienob.noveo.ui


import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.hienob.noveo.app.AppUiState
import ir.hienob.noveo.app.StartupState
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.NotificationSettings

internal enum class ThemePreset(val label: String) {
    SKY_LIGHT("Sky Light"),
    LIGHT("Light"),
    SUNSET_LIGHT("Sunset Light"),
    DARK("Dark"),
    OCEAN_DARK("Ocean Dark"),
    PLUM_DARK("Plum Dark"),
    OLED_DARK("OLED Dark"),
    SUNSET_SHIMMER("Sunset Shimmer"),
    CHERRY_RED("Cherry Red"),
    SNOWY_DAYDREAM("Snowy Daydream"),
    RAINBOW_RAGEBAIT("Rainbow Ragebait"),
    SANOKI_MEOA("Sanoki Meoa")
}

private val sunsetLightScheme = lightColorScheme(
    primary = Color(0xFFEA580C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFEDD5),
    onPrimaryContainer = Color(0xFF431407),
    secondary = Color(0xFFFB923C),
    background = Color(0xFFFFF7ED),
    surface = Color(0xFFFFFAF5),
    surfaceVariant = Color(0xFFFFEDD5),
    onSurface = Color(0xFF431407),
    onSurfaceVariant = Color(0xFF7C2D12),
    outline = Color(0xFFFDBA74)
)

private val plumDarkScheme = darkColorScheme(
    primary = Color(0xFFA855F7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2E1065),
    onPrimaryContainer = Color(0xFFFAF5FF),
    secondary = Color(0xFFD8B4FE),
    background = Color(0xFF14051F),
    surface = Color(0xFF1B0F2A),
    surfaceVariant = Color(0xFF2A1741),
    onSurface = Color(0xFFFAF5FF),
    onSurfaceVariant = Color(0xFFF3E8FF),
    outline = Color(0xFF4C1D95)
)

private val oledDarkScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0B0B0B),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF60A5FA),
    background = Color.Black,
    surface = Color.Black,
    surfaceVariant = Color(0xFF050505),
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFE5E5E5),
    outline = Color(0xFF1F1F1F)
)

private val sunsetShimmerScheme = darkColorScheme(
    primary = Color(0xFFF97316),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4A2311),
    onPrimaryContainer = Color(0xFFFFF7ED),
    secondary = Color(0xFFFB923C),
    background = Color(0xFF180B07),
    surface = Color(0xFF21110B),
    surfaceVariant = Color(0xFF2F170D),
    onSurface = Color(0xFFFFF7ED),
    onSurfaceVariant = Color(0xFFFED7AA),
    outline = Color(0xFF7C2D12)
)

private val cherryRedScheme = darkColorScheme(
    primary = Color(0xFFD2042D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF450a0a),
    onPrimaryContainer = Color(0xFFfecaca),
    secondary = Color(0xFFFF2C55),
    onSecondary = Color.White,
    background = Color(0xFF0c0a09),
    surface = Color(0xFF1c1917),
    surfaceVariant = Color(0xFF292524),
    onSurface = Color(0xFFf5f5f4),
    onSurfaceVariant = Color(0xFFd6d3d1),
    outline = Color(0xFF44403c)
)

private val snowyDaydreamScheme = lightColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE6FF),
    onPrimaryContainer = Color(0xFF102041),
    secondary = Color(0xFF7B94B8),
    background = Color(0xFFF1F7FF),
    surface = Color.White.copy(alpha = 0.86f),
    onSurface = Color(0xFF102041),
    outline = Color(0xFFBFDBFE)
)

private val rainbowRagebaitScheme = darkColorScheme(
    primary = Color(0xFFFF4FD8),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF331D52),
    onPrimaryContainer = Color(0xFFfdf4ff),
    secondary = Color(0xFFD8B4FE),
    onSecondary = Color(0xFF3b0764),
    background = Color(0xFF020617),
    surface = Color(0xFF0f172a),
    surfaceVariant = Color(0xFF1e293b),
    onSurface = Color(0xFFf8fafc),
    onSurfaceVariant = Color(0xFFcbd5e1),
    outline = Color(0xFF334155)
)

private val sanokiMeoaScheme = darkColorScheme(
    primary = Color(0xFFA855F7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF39145C),
    onPrimaryContainer = Color(0xFFf3e8ff),
    secondary = Color(0xFFC084FC),
    onSecondary = Color(0xFF4c1d95),
    background = Color(0xFF09090b),
    surface = Color(0xFF18181b),
    surfaceVariant = Color(0xFF27272a),
    onSurface = Color(0xFFf4f4f5),
    onSurfaceVariant = Color(0xFFd4d4d8),
    outline = Color(0xFF3f3f46)
)

private val skyLightScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFF60A5FA),
    background = Color(0xFFE0F2FE),
    surface = Color(0xFFF8FCFF),
    surfaceVariant = Color(0xFFECFEFF),
    onSurface = Color(0xFF082F49),
    onSurfaceVariant = Color(0xFF0F4C68),
    outline = Color(0xFFBAE6FD),
    error = Color(0xFFDC2626)
)

private val lightScheme = lightColorScheme(
    primary = Color(0xFF2563EB),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEFF6FF),
    onPrimaryContainer = Color(0xFF1E40AF),
    secondary = Color(0xFF60A5FA),
    background = Color(0xFFEEF4FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFF8FAFC),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFDBEAFE),
    error = Color(0xFFDC2626)
)

private val oceanDarkScheme = darkColorScheme(
    primary = Color(0xFF1D4ED8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF0F2742),
    onPrimaryContainer = Color(0xFFEFF6FF),
    secondary = Color(0xFF3B82F6),
    background = Color(0xFF071423),
    surface = Color(0xFF081A2F),
    surfaceVariant = Color(0xFF0C233D),
    onSurface = Color(0xFFEFF6FF),
    onSurfaceVariant = Color(0xFFDBEAFE),
    outline = Color(0xFF12324F),
    error = Color(0xFFEF4444)
)

private val darkScheme = darkColorScheme(
    primary = Color(0xFF3B82F6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFF8FAF4),
    secondary = Color(0xFF60A5FA),
    background = Color(0xFF020617),
    surface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFF111827),
    onSurface = Color(0xFFF8FAF4),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF1F2937),
    error = Color(0xFFEF4444)
)

@Composable
fun NoveoRoot(
    state: AppUiState,
    onDismissOnboarding: () -> Unit,
    onAuthMode: (Boolean) -> Unit,
    onStartRegisterCaptcha: (String, String) -> Unit,
    onAuthSubmit: (String, String) -> Unit,
    onOpenChat: (String) -> Unit,
    onStartDirectChat: (String) -> Unit,
    onStartCreateChat: (String, String, String?, String?) -> Unit,
    onSearchPublic: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onTyping: () -> Unit,
    onLogout: () -> Unit,
    onCaptchaTokenReceived: (String) -> Unit,
    onCaptchaDismiss: () -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onLoadOlder: () -> Unit,
    onReply: (ChatMessage?) -> Unit,
    onChangePassword: (String, String) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onSetLanguage: (String) -> Unit,
    onDismissUpdate: () -> Unit,
    onDownloadUpdate: () -> Unit,
    onInstallUpdate: () -> Unit,
    onCheckUpdate: () -> Unit,
    onUpdateNotificationSettings: (NotificationSettings) -> Unit,
    onRequestBatteryOptimization: () -> Unit
) {
    val strings = getStrings(state.languageCode)
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("noveo_ui", Context.MODE_PRIVATE) }
    val initialTheme = remember(prefs) {
        runCatching {
            ThemePreset.valueOf(
                prefs.getString("theme_preset", ThemePreset.SKY_LIGHT.name)
                    ?: ThemePreset.SKY_LIGHT.name
            )
        }.getOrElse { ThemePreset.SKY_LIGHT }
    }
    var currentTheme by rememberSaveable { mutableStateOf(initialTheme) }

    LaunchedEffect(currentTheme) {
        prefs.edit().putString("theme_preset", currentTheme.name).apply()
    }

    val colorScheme = when (currentTheme) {
        ThemePreset.SKY_LIGHT -> skyLightScheme
        ThemePreset.LIGHT -> lightScheme
        ThemePreset.SUNSET_LIGHT -> sunsetLightScheme
        ThemePreset.OCEAN_DARK -> oceanDarkScheme
        ThemePreset.DARK -> darkScheme
        ThemePreset.PLUM_DARK -> plumDarkScheme
        ThemePreset.OLED_DARK -> oledDarkScheme
        ThemePreset.SUNSET_SHIMMER -> sunsetShimmerScheme
        ThemePreset.CHERRY_RED -> cherryRedScheme
        ThemePreset.SNOWY_DAYDREAM -> snowyDaydreamScheme
        ThemePreset.RAINBOW_RAGEBAIT -> rainbowRagebaitScheme
        ThemePreset.SANOKI_MEOA -> sanokiMeoaScheme
    }

    val layoutDirection = LayoutDirection.Ltr
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        MaterialTheme(colorScheme = colorScheme) {
            Surface(modifier = Modifier.fillMaxSize()) {
                if (state.captchaInfo != null) {
                    CaptchaModal(
                        sessionId = state.captchaInfo.sessionId,
                        session = state.session,
                        onToken = onCaptchaTokenReceived,
                        onDismiss = onCaptchaDismiss
                    )
                }
                when (state.startupState) {
                    StartupState.Splash -> ConnectingShell(strings.brandName)
                    StartupState.Onboarding -> OnboardingScreen(strings, onDismissOnboarding)
                    StartupState.Auth -> AuthScreen(strings, state, onAuthMode, onStartRegisterCaptcha, onAuthSubmit)
                    StartupState.Home -> HomeScreen(
                        state = state,
                        onOpenChat = onOpenChat,
                        onStartDirectChat = onStartDirectChat,
                        onStartCreateChat = onStartCreateChat,
                        onSearchPublic = onSearchPublic,
                        onBackToChats = onBackToChats,
                        onSend = onSend,
                        onTyping = onTyping,
                        onLogout = onLogout,
                        onUpdateProfile = onUpdateProfile,
                        onLoadOlder = onLoadOlder,
                        onReply = onReply,
                        onChangePassword = onChangePassword,
                        onDeleteAccount = onDeleteAccount,
                        onSetLanguage = onSetLanguage,
                        onDismissUpdate = onDismissUpdate,
                        onDownloadUpdate = onDownloadUpdate,
                        onInstallUpdate = onInstallUpdate,
                        onCheckUpdate = onCheckUpdate,
                        onUpdateNotificationSettings = onUpdateNotificationSettings,
                        onRequestBatteryOptimization = onRequestBatteryOptimization,
                        currentTheme = currentTheme,

                        onThemeChange = { currentTheme = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectingShell(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AnimatedContent(
                targetState = title,
                label = "connection_title",
                transitionSpec = {
                    (slideInVertically(initialOffsetY = { it / 2 }) + fadeIn())
                        .togetherWith(slideOutVertically(targetOffsetY = { -it / 2 }) + fadeOut())
                        .using(SizeTransform(clip = false))
                }
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun OnboardingScreen(strings: NoveoStrings, onDismissOnboarding: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    val pages = strings.onboardingPages

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(strings.brandName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Text(pages[page], style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == page) 12.dp else 8.dp)
                        .background(
                            if (index == page) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
        Spacer(Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            if (page < pages.lastIndex) {
                Text(strings.skip, modifier = Modifier.clickable { onDismissOnboarding() }.padding(8.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                Button(onClick = { page += 1 }) { Text(strings.next) }
            } else {
                Spacer(Modifier.width(1.dp))
                Button(onClick = onDismissOnboarding) { Text(strings.onboardingAction) }
            }
        }
    }
}

@Composable
private fun AuthScreen(
    strings: NoveoStrings,
    state: AppUiState,
    onAuthMode: (Boolean) -> Unit,
    onStartRegisterCaptcha: (String, String) -> Unit,
    onAuthSubmit: (String, String) -> Unit
) {
    var handle by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(if (state.authModeSignup) strings.signupTitle else strings.loginTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(if (state.authModeSignup) strings.switchSignup else strings.switchLogin)
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(strings.loginButton, modifier = Modifier.clickable { onAuthMode(false) }.padding(8.dp), fontWeight = if (!state.authModeSignup) FontWeight.Bold else FontWeight.Normal)
            Text(strings.signupButton, modifier = Modifier.clickable { onAuthMode(true) }.padding(8.dp), fontWeight = if (state.authModeSignup) FontWeight.Bold else FontWeight.Normal)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = handle, onValueChange = { handle = it }, label = { Text(strings.handlePlaceholder) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text(strings.passwordPlaceholder) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                if (state.authModeSignup) {
                    onStartRegisterCaptcha(handle, password)
                } else {
                    onAuthSubmit(handle, password)
                }
            },
            enabled = !state.loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.authModeSignup) strings.signupButton else strings.loginButton)
        }
        state.error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun CaptchaModal(
    sessionId: String?,
    session: ir.hienob.noveo.data.Session?,
    onToken: (String) -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = { androidx.compose.material3.TextButton(onClick = onDismiss) { Text("Cancel") } },
        title = { Text("Solve Puzzle") },
        text = {
            Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
                androidx.compose.ui.viewinterop.AndroidView(
                    factory = { context ->
                        android.webkit.WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.userAgentString = "NoveoKotlin/0.3.4"
                            
                            webViewClient = object : android.webkit.WebViewClient() {
                                override fun shouldOverrideUrlLoading(view: android.webkit.WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                                    val url = request?.url?.toString() ?: ""
                                    if (url.contains("token=")) {
                                        onToken(url.substringAfter("token="))
                                        return true
                                    }
                                    return false
                                }

                                override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    // Handle captcha-parent-auth if needed via JS injection or postMessage
                                }
                            }
                            
                            addJavascriptInterface(object {
                                @android.webkit.JavascriptInterface
                                fun onCaptchaSolved(token: String) {
                                    onToken(token)
                                }
                                
                                @android.webkit.JavascriptInterface
                                fun notifyParent(dataJson: String) {
                                    val data = org.json.JSONObject(dataJson)
                                    if (data.optString("type") == "captcha-frame-ready") {
                                         // Post auth headers if needed
                                         val authHeaders = org.json.JSONObject()
                                         if (session != null) {
                                             authHeaders.put("X-User-ID", session.userId)
                                             authHeaders.put("X-Auth-Token", session.token)
                                         }
                                         val msg = org.json.JSONObject()
                                             .put("type", "captcha-parent-auth")
                                             .put("headers", authHeaders)
                                         
                                         post {
                                             evaluateJavascript("window.postMessage($msg, '*')", null)
                                         }
                                    }
                                }
                            }, "Android")

                            val baseUrl = "https://web.noveo.ir/puzzle.php"
                            val fullUrl = android.net.Uri.parse(baseUrl).buildUpon()
                                .appendQueryParameter("client", "kotlin")
                                .appendQueryParameter("platform", "android")
                                .apply { if (sessionId != null) appendQueryParameter("captchaSessionId", sessionId) }
                                .build().toString()

                            val headers = mutableMapOf<String, String>()
                            if (session != null) {
                                headers["X-User-ID"] = session.userId
                                headers["X-Auth-Token"] = session.token
                            }
                            headers["X-Noveo-Client"] = "kotlin"

                            loadUrl(fullUrl, headers)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    )
}
