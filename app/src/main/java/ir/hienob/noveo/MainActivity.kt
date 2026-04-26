package ir.hienob.noveo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import android.content.Intent
import ir.hienob.noveo.app.AppViewModel
import ir.hienob.noveo.data.NotificationSettings
import ir.hienob.noveo.ui.NoveoRoot

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        setContent {
            val state by viewModel.uiState.collectAsState()
            NoveoRoot(
                state = state,
                onDismissOnboarding = viewModel::dismissOnboarding,
                onAuthMode = viewModel::setAuthMode,
                onStartRegisterCaptcha = viewModel::startRegisterCaptcha,
                onAuthSubmit = { h, p -> viewModel.authenticate(h, p) },
                onOpenChat = viewModel::openChat,
                onStartDirectChat = viewModel::openDirectChat,
                onStartCreateChat = viewModel::startCreateChatCaptcha,
                onSearchPublic = viewModel::searchPublicDirectory,
                onBackToChats = viewModel::backToChatList,
                onSend = viewModel::sendMessage,
                onTyping = viewModel::sendTyping,
                onLogout = viewModel::logout,
                onAttachFile = viewModel::attachFile,
                onRemoveAttachment = viewModel::removeAttachment,
                onCaptchaTokenReceived = viewModel::onCaptchaTokenReceived,
                onCaptchaDismiss = viewModel::dismissCaptcha,
                onUpdateProfile = { u, b -> viewModel.updateProfile(u, b) },
                onLoadOlder = viewModel::loadOlderMessages,
                onReply = viewModel::setReplyingTo,
                onChangePassword = { o, n -> viewModel.changePassword(o, n) },
                onDeleteAccount = { p -> viewModel.deleteAccount(p) },
                onSetLanguage = { c -> viewModel.setLanguage(c) },
                onDismissUpdate = viewModel::dismissUpdate,
                onDownloadUpdate = viewModel::downloadUpdate,
                onInstallUpdate = viewModel::installUpdate,
                onCheckUpdate = { viewModel.checkForUpdate(manual = true) },
                onUpdateNotificationSettings = { viewModel.updateNotificationSettings(it) },
                onRequestBatteryOptimization = { viewModel.requestDisableBatteryOptimization() }
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.getStringExtra("chatId")?.let { chatId ->
            viewModel.openChat(chatId)
        }
    }
}

