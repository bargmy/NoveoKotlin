package ir.hienob.noveo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import ir.hienob.noveo.app.AppViewModel
import ir.hienob.noveo.ui.NoveoRoot

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val state by viewModel.uiState.collectAsState()
            NoveoRoot(
                state = state,
                onAuthMode = viewModel::setAuthMode,
                onAuthSubmit = viewModel::authenticate,
                onOpenChat = viewModel::openChat,
                onSend = viewModel::sendMessage,
                onLogout = viewModel::logout
            )
        }
    }
}
