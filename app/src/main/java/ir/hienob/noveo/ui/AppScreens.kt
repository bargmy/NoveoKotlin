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

internal enum class ThemePreset(val label: String) {
    SKY_LIGHT("Sky Light"),
    LIGHT("Light"),
    OCEAN_DARK("Ocean Dark"),
    DARK("Dark")
}

private val skyLightScheme = lightColorScheme(
    primary = Color(0xFF168BFF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6ECFF),
    onPrimaryContainer = Color(0xFF003A66),
    secondary = Color(0xFF52A7FF),
    background = Color(0xFFF1F8FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFE7F3FF),
    onSurface = Color(0xFF132238),
    onSurfaceVariant = Color(0xFF56708F),
    outline = Color(0xFFCEE2F6),
    error = Color(0xFFD14352)
)

private val lightScheme = lightColorScheme(
    primary = Color(0xFF2F80ED),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBFF),
    onPrimaryContainer = Color(0xFF0B3D91),
    secondary = Color(0xFF5B8DEF),
    background = Color(0xFFF5F9FF),
    surface = Color.White,
    surfaceVariant = Color(0xFFEDF4FF),
    onSurface = Color(0xFF162033),
    onSurfaceVariant = Color(0xFF5A6880),
    outline = Color(0xFFD2DEEF),
    error = Color(0xFFCC3344)
)

private val oceanDarkScheme = darkColorScheme(
    primary = Color(0xFF4DB4FF),
    onPrimary = Color(0xFF003A59),
    primaryContainer = Color(0xFF0D4C73),
    onPrimaryContainer = Color(0xFFD4F0FF),
    secondary = Color(0xFF81D0FF),
    background = Color(0xFF09131C),
    surface = Color(0xFF0E1B27),
    surfaceVariant = Color(0xFF132636),
    onSurface = Color(0xFFE1F4FF),
    onSurfaceVariant = Color(0xFF92B2C7),
    outline = Color(0xFF274257),
    error = Color(0xFFFF91A0)
)

private val darkScheme = darkColorScheme(
    primary = Color(0xFF74A7FF),
    onPrimary = Color(0xFF062B63),
    primaryContainer = Color(0xFF123A7C),
    onPrimaryContainer = Color(0xFFD7E6FF),
    secondary = Color(0xFF95B8FF),
    background = Color(0xFF0F141C),
    surface = Color(0xFF141B24),
    surfaceVariant = Color(0xFF1B2430),
    onSurface = Color(0xFFEAF1FF),
    onSurfaceVariant = Color(0xFF9CACBF),
    outline = Color(0xFF334258),
    error = Color(0xFFFF8A98)
)

@Composable
fun NoveoRoot(
    state: AppUiState,
    onDismissOnboarding: () -> Unit,
    onAuthMode: (Boolean) -> Unit,
    onAuthSubmit: (String, String) -> Unit,
    onOpenChat: (String) -> Unit,
    onStartDirectChat: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onLogout: () -> Unit
) {
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
        ThemePreset.OCEAN_DARK -> oceanDarkScheme
        ThemePreset.DARK -> darkScheme
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        MaterialTheme(colorScheme = colorScheme) {
            Surface(modifier = Modifier.fillMaxSize()) {
                when (state.startupState) {
                    StartupState.Splash -> ConnectingShell(state.connectionTitle)
                    StartupState.Onboarding -> OnboardingScreen(onDismissOnboarding)
                    StartupState.Auth -> AuthScreen(state, onAuthMode, onAuthSubmit)
                    StartupState.Home -> HomeScreen(
                        state = state,
                        onOpenChat = onOpenChat,
                        onStartDirectChat = onStartDirectChat,
                        onBackToChats = onBackToChats,
                        onSend = onSend,
                        onLogout = onLogout,
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
private fun OnboardingScreen(onDismissOnboarding: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    val pages = listOf(
        "Chat with your contacts in one place.",
        "Jump into conversations quickly with the Noveo mobile shell.",
        "Stay synced and start messaging as soon as you sign in."
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Noveo", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
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
                Text("Skip", modifier = Modifier.clickable { onDismissOnboarding() }.padding(8.dp), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                Button(onClick = { page += 1 }) { Text("Next") }
            } else {
                Spacer(Modifier.width(1.dp))
                Button(onClick = onDismissOnboarding) { Text("Get started") }
            }
        }
    }
}

@Composable
private fun AuthScreen(
    state: AppUiState,
    onAuthMode: (Boolean) -> Unit,
    onAuthSubmit: (String, String) -> Unit
) {
    var handle by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to Noveo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(if (state.authModeSignup) "Create your account to continue." else "Sign in to keep chatting.")
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Login", modifier = Modifier.clickable { onAuthMode(false) }.padding(8.dp), fontWeight = if (!state.authModeSignup) FontWeight.Bold else FontWeight.Normal)
            Text("Sign Up", modifier = Modifier.clickable { onAuthMode(true) }.padding(8.dp), fontWeight = if (state.authModeSignup) FontWeight.Bold else FontWeight.Normal)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = handle, onValueChange = { handle = it }, label = { Text("Handle") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onAuthSubmit(handle, password) }, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) {
            Text(if (state.authModeSignup) "Create account" else "Continue")
        }
        state.error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
