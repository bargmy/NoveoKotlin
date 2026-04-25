package ir.hienob.noveo.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ProcessLifecycleOwner
import ir.hienob.noveo.MainActivity
import ir.hienob.noveo.R
import ir.hienob.noveo.core.notifications.NotificationChannels
import ir.hienob.noveo.data.ChatMessage
import ir.hienob.noveo.data.ChatSocket
import ir.hienob.noveo.data.NotificationSettings
import ir.hienob.noveo.data.Session
import ir.hienob.noveo.data.SessionStore
import ir.hienob.noveo.data.SocketEvent
import ir.hienob.noveo.data.UserSummary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class NoveoNotificationService : LifecycleService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var sessionStore: SessionStore
    private val socket = ChatSocket()
    private var socketJob: Job? = null
    
    companion object {
        private val _socketEvents = MutableSharedFlow<SocketEvent>(extraBufferCapacity = 100)
        val socketEvents = _socketEvents.asSharedFlow()
        
        var isAppInForeground = false
            private set
            
        private var activeSession: Session? = null
        private var instance: NoveoNotificationService? = null
        
        // Track known users in service for notification name resolution
        private val knownUsers = mutableMapOf<String, ir.hienob.noveo.data.UserSummary>()

        fun updateKnownUsers(users: Map<String, ir.hienob.noveo.data.UserSummary>) {
            knownUsers.putAll(users)
        }

        fun send(payload: JSONObject): Boolean {
            return instance?.socket?.send(payload) ?: false
        }

        fun start(context: Context) {
            val intent = Intent(context, NoveoNotificationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        sessionStore = SessionStore(this)
        setupForeground()
        
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_START -> {
                        isAppInForeground = true
                        updatePresence(true)
                    }
                    Lifecycle.Event.ON_STOP -> {
                        isAppInForeground = false
                        updatePresence(false)
                    }
                    else -> {}
                }
            }
        })
        
        serviceScope.launch {
            sessionStore.read()?.let { session ->
                activeSession = session
                connectSocket(session)
            }
        }
    }

    private fun setupForeground() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationChannels.SERVICE,
                "Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, NotificationChannels.SERVICE)
            .setContentTitle("Noveo is running")
            .setContentText("Listening for messages")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    private fun connectSocket(session: Session) {
        socketJob?.cancel()
        socketJob = serviceScope.launch {
            socket.connect(session) { knownUsers }.collect { event ->
                _socketEvents.emit(event)
                if (event is SocketEvent.NewMessage && !isAppInForeground) {
                    if (event.message.senderId == activeSession?.userId) return@collect
                    val settings = sessionStore.readNotificationSettings()
                    if (settings.enabled) {
                        val shouldNotify = when (event.message.chatType) {
                            "private" -> settings.dms
                            "group" -> settings.groups
                            "channel" -> settings.channels
                            else -> true
                        }
                        if (shouldNotify) {
                            showNotification(event.message)
                        }
                    }
                }
            }
        }
    }

    private fun updatePresence(online: Boolean) {
        val session = activeSession ?: return
        serviceScope.launch(Dispatchers.IO) {
            val payload = JSONObject()
                .put("type", "presence_update")
                .put("online", online)
            socket.send(payload)
        }
    }

    private fun showNotification(message: ChatMessage) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NotificationChannels.MESSAGES,
                "Messages",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("chatId", message.chatId)
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Reply Action
        val remoteInput = RemoteInput.Builder("key_text_reply")
            .setLabel("Reply...")
            .build()
        
        val replyIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ir.hienob.noveo.ACTION_REPLY"
            putExtra("chatId", message.chatId)
            putExtra("messageId", message.id)
        }
        val replyPendingIntent = PendingIntent.getBroadcast(this, message.chatId.hashCode(), replyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        
        val replyAction = NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            "Reply",
            replyPendingIntent
        ).addRemoteInput(remoteInput).build()

        // Seen Action
        val seenIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "ir.hienob.noveo.ACTION_SEEN"
            putExtra("chatId", message.chatId)
            putExtra("messageId", message.id)
        }
        val seenPendingIntent = PendingIntent.getBroadcast(this, message.chatId.hashCode() + 1, seenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        
        val seenAction = NotificationCompat.Action.Builder(
            0,
            "Mark as Read",
            seenPendingIntent
        ).build()

        val notification = NotificationCompat.Builder(this, NotificationChannels.MESSAGES)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.senderName)
            .setContentText(message.content.previewText())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .addAction(replyAction)
            .addAction(seenAction)
            .build()

        notificationManager.notify(message.chatId.hashCode(), notification)
    }

    override fun onDestroy() {
        socketJob?.cancel()
        instance = null
        super.onDestroy()
    }
}
