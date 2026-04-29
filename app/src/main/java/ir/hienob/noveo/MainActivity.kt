package ir.hienob.noveo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import android.content.Intent
import ir.hienob.noveo.app.AppViewModel
import ir.hienob.noveo.ui.NoveoRoot

import androidx.activity.result.contract.ActivityResultContracts
import android.Manifest
import android.os.Build

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val currentSettings = viewModel.uiState.value.notificationSettings
            viewModel.updateNotificationSettings(currentSettings.copy(enabled = true))
        }
    }

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
                onEditMessage = viewModel::setEditingMessage,
                onForwardMessage = viewModel::setForwardingMessage,
                onForwardConfirm = viewModel::forwardMessage,
                onToggleReaction = viewModel::toggleReaction,
                onDeleteMessage = viewModel::deleteMessage,
                onPinMessage = viewModel::pinMessage,
                onChangePassword = { o, n -> viewModel.changePassword(o, n) },
                onDeleteAccount = { p -> viewModel.deleteAccount(p) },
                onSetLanguage = { c -> viewModel.setLanguage(c) },
                onDismissUpdate = viewModel::dismissUpdate,
                onDownloadUpdate = viewModel::downloadUpdate,
                onInstallUpdate = viewModel::installUpdate,
                onCheckUpdate = { viewModel.checkForUpdate(manual = true) },
                onSetBetaUpdatesEnabled = viewModel::setBetaUpdatesEnabled,
                onSetDoubleTapReaction = viewModel::setDoubleTapReaction,
                onUpdateNotificationSettings = { viewModel.updateNotificationSettings(it) },
                onRequestBatteryOptimization = { viewModel.requestDisableBatteryOptimization() },
                onRequestPermission = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                },
                onPlayAudio = { viewModel.playAudio(it) },
                onPauseAudio = { viewModel.pauseAudio() },
                onResumeAudio = { viewModel.resumeAudio() },
                onStopAudio = { viewModel.stopAudio() },
                onSeekAudio = { viewModel.seekAudio(it) },
                onDownloadFile = { viewModel.downloadFile(it) },
                onCall = { viewModel.startOutgoingCall(it) },
                onAcceptCall = { c, i -> viewModel.acceptCall(c, i) },
                onDeclineCall = viewModel::declineCall,
                onLeaveCall = viewModel::leaveCall,
                onToggleMute = viewModel::toggleMute,
                onToggleDeafen = viewModel::toggleDeafen,
                onCancelUpload = viewModel::cancelPendingUpload,
                onSendSticker = { sticker -> viewModel.sendSticker(sticker) },
                onAddSavedSticker = { message -> viewModel.addSavedStickerFromMessage(message) }
            )
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkBatteryOptimization()
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

