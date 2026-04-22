package ir.hienob.noveo.ui

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import ir.hienob.noveo.app.AppUiState
import ir.hienob.noveo.app.StartupState

private val noveoBlueScheme = lightColorScheme(
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
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        MaterialTheme(colorScheme = noveoBlueScheme) {
            Surface(modifier = Modifier.fillMaxSize()) {
                when (state.startupState) {
                    StartupState.Splash -> SplashScreen()
                    StartupState.Onboarding -> OnboardingScreen(onDismissOnboarding)
                    StartupState.Auth -> AuthScreen(state, onAuthMode, onAuthSubmit)
                    StartupState.Home -> HomeScreen(state, onOpenChat, onStartDirectChat, onBackToChats, onSend, onLogout)
                }
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
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
            if (state.loading) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) else Text(if (state.authModeSignup) "Create account" else "Continue")
        }
        state.error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
