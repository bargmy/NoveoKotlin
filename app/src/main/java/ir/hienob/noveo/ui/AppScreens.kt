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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
    val prefs = remember(context) {
        context.getSharedPreferences("noveo_ui", Context.MODE_PRIVATE)
    }
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
                AnimatedContent(
                    targetState = state.startupState,
                    transitionSpec = {
                        (fadeIn() + slideInVertically { it / 5 }) togetherWith
                            (fadeOut() + slideOutVertically { -it / 8 }) using
                            SizeTransform(clip = false)
                    },
                    label = "startup"
                ) { startup ->
                    when (startup) {
                        StartupState.Splash -> SplashScreen()
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
}

@Composable
private fun SplashScreen() {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0C2547), Color(0xFF168BFF), Color(0xFFD6ECFF))
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "N",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Noveo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(Modifier.height(10.dp))
            CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp)
        }
    }
}

@Composable
private fun OnboardingScreen(onDismissOnboarding: () -> Unit) {
    var page by remember { mutableStateOf(0) }
    val pages = listOf(
        Triple(
            "Chat smarter",
            "Talk with your contacts, groups, and channels in one clean mobile home.",
            Icons.Outlined.ChatBubbleOutline
        ),
        Triple(
            "Control your profile",
            "Switch themes, review your account, and manage your presence from settings.",
            Icons.Outlined.PersonOutline
        ),
        Triple(
            "Private by default",
            "Sign in fast and keep your conversations protected with a focused interface.",
            Icons.Outlined.Lock
        )
    )
    val item = pages[page]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(8.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(104.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.third,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                text = item.first,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(14.dp))
            Text(
                text = item.second,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(pages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == page) 14.dp else 10.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == page) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    if (page < pages.lastIndex) page += 1 else onDismissOnboarding()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (page < pages.lastIndex) "Next" else "Get started")
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (page < pages.lastIndex) "Skip" else "Back",
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable {
                        if (page < pages.lastIndex) onDismissOnboarding() else page -= 1
                    }
                    .padding(horizontal = 18.dp, vertical = 10.dp),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(22.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.authModeSignup) Icons.Outlined.AlternateEmail else Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            if (state.authModeSignup) "Create account" else "Welcome back",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            if (state.authModeSignup) "Join Noveo to start chatting instantly."
                            else "Sign in to continue your conversations.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AuthModeChip(
                        label = "Login",
                        selected = !state.authModeSignup,
                        onClick = { onAuthMode(false) }
                    )
                    AuthModeChip(
                        label = "Sign Up",
                        selected = state.authModeSignup,
                        onClick = { onAuthMode(true) }
                    )
                }
                Spacer(Modifier.height(18.dp))
                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("Handle") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )
                Spacer(Modifier.height(18.dp))
                Button(
                    onClick = { onAuthSubmit(handle, password) },
                    enabled = !state.loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.loading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Text(if (state.authModeSignup) "Create account" else "Continue")
                    }
                }
                state.error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun AuthModeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun HomeScreen(
    state: AppUiState,
    onOpenChat: (String) -> Unit,
    onStartDirectChat: (String) -> Unit,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit,
    onLogout: () -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Noveo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    text = state.currentUser?.name ?: "Signed in",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ThemeToggleButton(currentTheme = currentTheme, onThemeChange = onThemeChange)
                IconButton(onClick = onLogout) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Logout")
                }
            }
        }
        Row(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .width(132.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Chats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
                state.chats.forEach { chat ->
                    val selected = state.selectedChatId == chat.id
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { onOpenChat(chat.id) },
                        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                            Text(chat.title, maxLines = 1, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(2.dp))
                            Text(
                                chat.kind.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            if (state.selectedChatId == null) {
                EmptyHomePane(
                    state = state,
                    onStartDirectChat = onStartDirectChat,
                    currentTheme = currentTheme,
                    onThemeChange = onThemeChange,
                    onLogout = onLogout
                )
            } else {
                ChatPane(state = state, onBackToChats = onBackToChats, onSend = onSend)
            }
        }
    }
}

@Composable
private fun ThemeToggleButton(
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit
) {
    val nextTheme = when (currentTheme) {
        ThemePreset.SKY_LIGHT -> ThemePreset.OCEAN_DARK
        ThemePreset.OCEAN_DARK -> ThemePreset.LIGHT
        ThemePreset.LIGHT -> ThemePreset.DARK
        ThemePreset.DARK -> ThemePreset.SKY_LIGHT
    }
    val icon = when (currentTheme) {
        ThemePreset.SKY_LIGHT -> Icons.Outlined.WbSunny
        ThemePreset.LIGHT -> Icons.Outlined.LightMode
        ThemePreset.OCEAN_DARK -> Icons.Outlined.DarkMode
        ThemePreset.DARK -> Icons.Outlined.DarkMode
    }
    IconButton(onClick = { onThemeChange(nextTheme) }) {
        Icon(icon, contentDescription = "Switch theme")
    }
}

@Composable
private fun EmptyHomePane(
    state: AppUiState,
    onStartDirectChat: (String) -> Unit,
    currentTheme: ThemePreset,
    onThemeChange: (ThemePreset) -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.78f)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Group,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Pick a chat or start a new conversation",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "You have ${state.contacts.size} contacts ready. Tap any contact below to open a direct chat.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.contacts.take(4).forEach { contact ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onStartDirectChat(contact.handle) },
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(contact.name.take(1), fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(contact.name, fontWeight = FontWeight.SemiBold)
                                Text(
                                    "@${contact.handle}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null)
                        }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AuthModeChip(
                    label = currentTheme.label,
                    selected = true,
                    onClick = { onThemeChange(when (currentTheme) {
                        ThemePreset.SKY_LIGHT -> ThemePreset.OCEAN_DARK
                        ThemePreset.OCEAN_DARK -> ThemePreset.LIGHT
                        ThemePreset.LIGHT -> ThemePreset.DARK
                        ThemePreset.DARK -> ThemePreset.SKY_LIGHT
                    }) }
                )
                AuthModeChip(label = "Logout", selected = false, onClick = onLogout)
            }
        }
    }
}

@Composable
private fun ChatPane(
    state: AppUiState,
    onBackToChats: () -> Unit,
    onSend: (String) -> Unit
) {
    val chat = state.selectedChat ?: return
    var draft by remember(chat.id) { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Back",
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .clickable(onClick = onBackToChats)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(chat.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        chat.kind.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(Icons.Outlined.PersonOutline, contentDescription = null)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chat.messages.takeLast(10).forEach { message ->
                val mine = message.senderHandle == state.currentUser?.handle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (mine) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 22.dp,
                            topEnd = 22.dp,
                            bottomStart = if (mine) 22.dp else 8.dp,
                            bottomEnd = if (mine) 8.dp else 22.dp
                        ),
                        color = if (mine) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        tonalElevation = 1.dp
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            if (!mine) {
                                Text(
                                    message.senderName,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(2.dp))
                            }
                            Text(message.text)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                message.timestamp,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = draft,
                    onValueChange = { draft = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Write a message") },
                    shape = RoundedCornerShape(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Button(
                    onClick = {
                        val trimmed = draft.trim()
                        if (trimmed.isNotEmpty()) {
                            onSend(trimmed)
                            draft = ""
                        }
                    },
                    enabled = draft.isNotBlank()
                ) {
                    Text("Send")
                }
            }
        }
    }
}
