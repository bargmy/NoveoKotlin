package ir.hienob.noveo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ir.hienob.noveo.app.AppUiState
import ir.hienob.noveo.app.StartupState
import ir.hienob.noveo.data.ChatSummary

@Composable
fun NoveoRoot(
    state: AppUiState,
    onAuthMode: (Boolean) -> Unit,
    onAuthSubmit: (String, String) -> Unit,
    onOpenChat: (Long) -> Unit,
    onSend: (String) -> Unit,
    onLogout: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        when (state.startupState) {
            StartupState.Splash -> SplashScreen()
            StartupState.Auth -> AuthScreen(state, onAuthMode, onAuthSubmit)
            StartupState.Home -> HomeScreen(state, onOpenChat, onSend, onLogout)
        }
    }
}

@Composable
private fun SplashScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
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

    Column(modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.Center) {
        Row {
            Text(
                text = "Login",
                modifier = Modifier.clickable { onAuthMode(false) }.padding(8.dp),
                fontWeight = if (!state.authModeSignup) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = "Sign Up",
                modifier = Modifier.clickable { onAuthMode(true) }.padding(8.dp),
                fontWeight = if (state.authModeSignup) FontWeight.Bold else FontWeight.Normal
            )
        }
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = handle, onValueChange = { handle = it }, label = { Text("Handle") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onAuthSubmit(handle, password) }, enabled = !state.loading, modifier = Modifier.fillMaxWidth()) {
            if (state.loading) CircularProgressIndicator(modifier = Modifier.width(20.dp)) else Text(if (state.authModeSignup) "Create account" else "Continue")
        }
        state.error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
private fun HomeScreen(state: AppUiState, onOpenChat: (Long) -> Unit, onSend: (String) -> Unit, onLogout: () -> Unit) {
    Row(modifier = Modifier.fillMaxSize()) {
        ChatListPane(
            chats = state.chats,
            selectedChatId = state.selectedChatId,
            onOpenChat = onOpenChat,
            onLogout = onLogout,
            modifier = Modifier.width(144.dp).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant)
        )
        ChatPane(
            state = state,
            onSend = onSend,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ChatListPane(
    chats: List<ChatSummary>,
    selectedChatId: Long?,
    onOpenChat: (Long) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(8.dp)) {
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth()) { Text("Logout") }
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(chats) { chat ->
                val active = chat.id == selectedChatId
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenChat(chat.id) }
                        .background(if (active) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {
                    Text(chat.title, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Medium)
                    Text(chat.lastMessage, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
                    if (chat.unreadCount > 0) {
                        Text("${chat.unreadCount}", style = MaterialTheme.typography.labelSmall)
                    }
                }
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun ChatPane(state: AppUiState, onSend: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    Column(modifier = modifier.padding(12.dp)) {
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(state.messages) { msg ->
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(msg.sender, style = MaterialTheme.typography.labelSmall)
                    Text(msg.text)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f), label = { Text("Message") })
            Spacer(Modifier.width(8.dp))
            Button(onClick = { onSend(text); text = "" }) { Text("Send") }
        }
    }
}
