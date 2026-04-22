package ir.hienob.noveo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ir.hienob.noveo.app.AppViewModel
import ir.hienob.noveo.ui.NoveoRoot

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val state by viewModel.uiState.collectAsState()
            NoveoRoot(
                state = state,
                onDismissOnboarding = viewModel::dismissOnboarding,
                onAuthMode = viewModel::setAuthMode,
                onAuthSubmit = viewModel::authenticate,
                onOpenChat = viewModel::openChat,
                onStartDirectChat = viewModel::openDirectChat,
                onSearchPublic = viewModel::searchPublicDirectory,
                onBackToChats = viewModel::backToChatList,
                onSend = viewModel::sendMessage,
                onLogout = viewModel::logout
            )
        }
    }
}
