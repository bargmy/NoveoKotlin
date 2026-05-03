import org.gradle.api.GradleException

val patchNoveoChatUi by tasks.registering {
    doLast {
        fun String.patchOnce(name: String, old: String, new: String): String {
            if (contains(new)) return this
            if (!contains(old)) throw GradleException("Unable to apply source patch: $name")
            return replace(old, new)
        }

        fun String.patchMaybe(old: String, new: String): String {
            if (contains(new)) return this
            if (!contains(old)) return this
            return replace(old, new)
        }

        fun patchSource(relativePath: String, transform: (String) -> String) {
            val target = file(relativePath)
            val original = target.readText()
            val patched = transform(original)
            if (patched != original) target.writeText(patched)
        }

        patchSource("src/main/java/ir/hienob/noveo/ui/Localization.kt") { source ->
            source
                .patchOnce(
                    "members count format field",
                    "val membersCount: String = \"members\",\n    val membersOnline: String = \"online\",",
                    "val membersCount: String = \"members\",\n    val membersCountFormat: String = \"%d members\",\n    val membersOnline: String = \"online\","
                )
                .patchMaybe("membersCount = \"Mitglieder\",\n        membersOnline = \"online\",", "membersCount = \"Mitglieder\",\n        membersCountFormat = \"%d Mitglieder\",\n        membersOnline = \"online\",")
                .patchMaybe("membersCount = \"участников\",\n        membersOnline = \"в сети\",", "membersCount = \"участников\",\n        membersCountFormat = \"%d участников\",\n        membersOnline = \"в сети\",")
                .patchMaybe("membersCount = \"成员\",\n        membersOnline = \"在线\",", "membersCount = \"成员\",\n        membersCountFormat = \"%d 位成员\",\n        membersOnline = \"在线\",")
                .patchMaybe("membersCount = \"اعضا\",\n        membersOnline = \"آنلاین\",", "membersCount = \"اعضا\",\n        membersCountFormat = \"%d عضو\",\n        membersOnline = \"آنلاین\",")
                .patchMaybe("membersCount = \"membres\",\n        membersOnline = \"en ligne\",", "membersCount = \"membres\",\n        membersCountFormat = \"%d membres\",\n        membersOnline = \"en ligne\",")
        }

        patchSource("src/main/java/ir/hienob/noveo/ui/HomeUi.kt") { source ->
            source
                .patchOnce(
                    "members count formatter helper",
                    "private fun formatLastSeen(lastSeen: Long?, strings: NoveoStrings): String {",
                    "private fun formatMembersCount(count: Int, strings: NoveoStrings): String = strings.membersCountFormat.format(count)\n\nprivate fun formatLastSeen(lastSeen: Long?, strings: NoveoStrings): String {"
                )
                .patchOnce(
                    "sliding chat lock state",
                    "var chatSnapshotCapturedForGesture by remember { mutableStateOf(false) }",
                    "var chatSnapshotCapturedForGesture by remember { mutableStateOf(false) }\n    var lockedSlidingChatId by remember { mutableStateOf<String?>(null) }\n    var lockedSlidingChat by remember { mutableStateOf<ChatSummary?>(null) }\n    var lockedSlidingMessages by remember { mutableStateOf<List<ChatMessage>?>(null) }\n    var completingBackSwipe by remember { mutableStateOf(false) }"
                )
                .patchOnce(
                    "selected chat reset guard",
                    "LaunchedEffect(state.selectedChatId) {\n        chatBackOffset.snapTo(0f)\n        chatSnapshot = null\n        chatSnapshotCapturedForGesture = false\n    }",
                    "LaunchedEffect(state.selectedChatId) {\n        if (!completingBackSwipe) {\n            chatBackOffset.snapTo(0f)\n            chatSnapshot = null\n            chatSnapshotCapturedForGesture = false\n            lockedSlidingChatId = null\n            lockedSlidingChat = null\n            lockedSlidingMessages = null\n        }\n    }"
                )
                .patchOnce(
                    "locked chat pane state",
                    "val selectedChat = state.chats.firstOrNull { it.id == state.selectedChatId }\n    val selectedProfile = remember(profileUserId, state.usersById) { profileUserId?.let(state.usersById::get) }",
                    "val selectedChat = state.chats.firstOrNull { it.id == state.selectedChatId }\n    val chatPaneSelectedChat = lockedSlidingChat ?: selectedChat\n    val chatPaneState = lockedSlidingMessages?.let { messages ->\n        state.copy(selectedChatId = lockedSlidingChatId ?: state.selectedChatId, messages = messages)\n    } ?: state\n    val selectedProfile = remember(profileUserId, state.usersById) { profileUserId?.let(state.usersById::get) }"
                )
                .patchOnce(
                    "capture sliding chat",
                    "if (allowChatBackDrag && !chatSnapshotCapturedForGesture) {",
                    "if (allowChatBackDrag) {\n                                lockedSlidingChatId = state.selectedChatId\n                                lockedSlidingChat = selectedChat\n                                lockedSlidingMessages = state.messages\n                            }\n                            if (allowChatBackDrag && !chatSnapshotCapturedForGesture) {"
                )
                .patchOnce(
                    "two phase compact swipe",
                    "if (compact && state.selectedChatId != null) {\n                                    val total = chatBackOffset.value\n                                    if (total > 150) {\n                                        onBackToChats()\n                                    }\n                                    chatBackOffset.animateTo(0f)\n                                    chatSnapshotCapturedForGesture = false\n                                    chatSnapshot = null\n                                } else if (allowSidebarSwipe) {",
                    "if (compact && (state.selectedChatId != null || lockedSlidingChatId != null)) {\n                                    val total = chatBackOffset.value\n                                    if (total > 150) {\n                                        completingBackSwipe = true\n                                        chatBackOffset.animateTo(size.width.toFloat().coerceAtLeast(total), tween(180, easing = FastOutSlowInEasing))\n                                        onBackToChats()\n                                        chatBackOffset.snapTo(0f)\n                                    } else {\n                                        chatBackOffset.animateTo(0f)\n                                    }\n                                    completingBackSwipe = false\n                                    chatSnapshotCapturedForGesture = false\n                                    chatSnapshot = null\n                                    lockedSlidingChatId = null\n                                    lockedSlidingChat = null\n                                    lockedSlidingMessages = null\n                                } else if (allowSidebarSwipe) {"
                )
                .patchOnce("locked translation", "val chatTranslation = if (state.selectedChatId != null) chatBackOffset.value else 0f", "val chatTranslation = if (state.selectedChatId != null || lockedSlidingChatId != null) chatBackOffset.value else 0f")
                .patchOnce("locked snapshot", "if (state.selectedChatId != null && chatTranslation > 0f) {", "if ((state.selectedChatId != null || lockedSlidingChatId != null) && chatTranslation > 0f) {")
                .replace("state = state,\n                                compact = true,\n                                strings = strings,\n                                selectedChat = selectedChat,", "state = chatPaneState,\n                                compact = true,\n                                strings = strings,\n                                selectedChat = chatPaneSelectedChat,")
                .replace("state = state,\n                                compact = false,\n                                strings = strings,\n                                selectedChat = selectedChat,", "state = chatPaneState,\n                                compact = false,\n                                strings = strings,\n                                selectedChat = chatPaneSelectedChat,")
                .replace("selectedChat?.id?.let {", "chatPaneSelectedChat?.id?.let {")
                .replace("onCall = { selectedChat?.id?.let { onCall(it) } },", "onCall = { chatPaneSelectedChat?.id?.let { onCall(it) } },")
                .replace("onCancelUpload = { selectedChat?.id?.let { onCancelUpload(it) } },", "onCancelUpload = { chatPaneSelectedChat?.id?.let { onCancelUpload(it) } },")
                .replace("\${chat.memberIds.size} \${strings.membersCount}", "\${formatMembersCount(chat.memberIds.size, strings)}")
                .replace("\${selectedChat.memberIds.size} \${strings.membersCount}", "\${formatMembersCount(selectedChat.memberIds.size, strings)}")
                .replace("\"\${memberCount} members\"", "formatMembersCount(memberCount, strings)")
                .replace("\"\${chat.memberIds.size} members\"", "formatMembersCount(chat.memberIds.size, strings)")
                .replace("\"\${selectedChat.memberIds.size} members\"", "formatMembersCount(selectedChat.memberIds.size, strings)")
        }

        patchSource("src/main/java/ir/hienob/noveo/app/AppViewModel.kt") { source ->
            source.patchOnce(
                "optimistic leave navigation",
                "withContext(Dispatchers.IO) { api.leaveChat(session, chatId) }\n                if (_uiState.value.selectedChatId == chatId) {\n                    backToChatList()\n                }\n                refreshHomeSilently()",
                "withContext(Dispatchers.IO) { api.leaveChat(session, chatId) }\n                val current = _uiState.value\n                val updatedChats = current.chats.map { chat ->\n                    if (chat.id == chatId) chat.copy(\n                        memberIds = chat.memberIds.filterNot { it == session.userId },\n                        canChat = false\n                    ) else chat\n                }\n                _uiState.value = current.copy(chats = updatedChats)\n                if (current.selectedChatId == chatId) {\n                    backToChatList()\n                }\n                refreshHomeSilently()"
            )
        }
    }
}

tasks.matching { it.name.startsWith("compile") || it.name.startsWith("pre") }.configureEach {
    dependsOn(patchNoveoChatUi)
}
