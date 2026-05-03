#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parent


def patch_once(source: str, name: str, old: str, new: str) -> str:
    if new in source:
        return source
    if old not in source:
        raise RuntimeError(f"Unable to apply source patch: {name}")
    return source.replace(old, new)


def patch_maybe(source: str, old: str, new: str) -> str:
    if new in source:
        return source
    if old not in source:
        return source
    return source.replace(old, new)


def patch_file(relative_path: str, transform) -> None:
    target = ROOT / relative_path
    original = target.read_text(encoding="utf-8")
    patched = transform(original)
    if patched != original:
        target.write_text(patched, encoding="utf-8")


def patch_localization(source: str) -> str:
    source = patch_once(
        source,
        "members count format field",
        'val membersCount: String = "members",\n    val membersOnline: String = "online",',
        'val membersCount: String = "members",\n    val membersCountFormat: String = "%d members",\n    val membersOnline: String = "online",',
    )
    replacements = [
        ('membersCount = "Mitglieder",\n        membersOnline = "online",', 'membersCount = "Mitglieder",\n        membersCountFormat = "%d Mitglieder",\n        membersOnline = "online",'),
        ('membersCount = "участников",\n        membersOnline = "в сети",', 'membersCount = "участников",\n        membersCountFormat = "%d участников",\n        membersOnline = "в сети",'),
        ('membersCount = "成员",\n        membersOnline = "在线",', 'membersCount = "成员",\n        membersCountFormat = "%d 位成员",\n        membersOnline = "在线",'),
        ('membersCount = "اعضا",\n        membersOnline = "آنلاین",', 'membersCount = "اعضا",\n        membersCountFormat = "%d عضو",\n        membersOnline = "آنلاین",'),
        ('membersCount = "membres",\n        membersOnline = "en ligne",', 'membersCount = "membres",\n        membersCountFormat = "%d membres",\n        membersOnline = "en ligne",'),
    ]
    for old, new in replacements:
        source = patch_maybe(source, old, new)
    return source


def patch_home_ui(source: str) -> str:
    source = patch_once(
        source,
        "members count formatter helper",
        "private fun formatLastSeen(lastSeen: Long?, strings: NoveoStrings): String {",
        "private fun formatMembersCount(count: Int, strings: NoveoStrings): String = strings.membersCountFormat.format(count)\n\nprivate fun formatLastSeen(lastSeen: Long?, strings: NoveoStrings): String {",
    )
    source = patch_once(
        source,
        "sliding chat lock state",
        "var chatSnapshotCapturedForGesture by remember { mutableStateOf(false) }",
        "var chatSnapshotCapturedForGesture by remember { mutableStateOf(false) }\n    var lockedSlidingChatId by remember { mutableStateOf<String?>(null) }\n    var lockedSlidingChat by remember { mutableStateOf<ChatSummary?>(null) }\n    var lockedSlidingMessages by remember { mutableStateOf<List<ChatMessage>?>(null) }\n    var completingBackSwipe by remember { mutableStateOf(false) }",
    )
    source = patch_once(
        source,
        "selected chat reset guard",
        "LaunchedEffect(state.selectedChatId) {\n        chatBackOffset.snapTo(0f)\n        chatSnapshot = null\n        chatSnapshotCapturedForGesture = false\n    }",
        "LaunchedEffect(state.selectedChatId) {\n        if (!completingBackSwipe) {\n            chatBackOffset.snapTo(0f)\n            chatSnapshot = null\n            chatSnapshotCapturedForGesture = false\n            lockedSlidingChatId = null\n            lockedSlidingChat = null\n            lockedSlidingMessages = null\n        }\n    }",
    )
    source = patch_once(
        source,
        "locked chat pane state",
        "val selectedChat = state.chats.firstOrNull { it.id == state.selectedChatId }\n    val selectedProfile = remember(profileUserId, state.usersById) { profileUserId?.let(state.usersById::get) }",
        "val selectedChat = state.chats.firstOrNull { it.id == state.selectedChatId }\n    val chatPaneSelectedChat = lockedSlidingChat ?: selectedChat\n    val chatPaneState = lockedSlidingMessages?.let { messages ->\n        state.copy(selectedChatId = lockedSlidingChatId ?: state.selectedChatId, messages = messages)\n    } ?: state\n    val selectedProfile = remember(profileUserId, state.usersById) { profileUserId?.let(state.usersById::get) }",
    )
    source = patch_once(
        source,
        "capture sliding chat",
        "if (allowChatBackDrag && !chatSnapshotCapturedForGesture) {",
        "if (allowChatBackDrag) {\n                                lockedSlidingChatId = state.selectedChatId\n                                lockedSlidingChat = selectedChat\n                                lockedSlidingMessages = state.messages\n                            }\n                            if (allowChatBackDrag && !chatSnapshotCapturedForGesture) {",
    )
    source = patch_once(
        source,
        "two phase compact swipe",
        "if (compact && state.selectedChatId != null) {\n                                    val total = chatBackOffset.value\n                                    if (total > 150) {\n                                        onBackToChats()\n                                    }\n                                    chatBackOffset.animateTo(0f)\n                                    chatSnapshotCapturedForGesture = false\n                                    chatSnapshot = null\n                                } else if (allowSidebarSwipe) {",
        "if (compact && (state.selectedChatId != null || lockedSlidingChatId != null)) {\n                                    val total = chatBackOffset.value\n                                    if (total > 150) {\n                                        completingBackSwipe = true\n                                        chatBackOffset.animateTo(size.width.toFloat().coerceAtLeast(total), tween(180, easing = FastOutSlowInEasing))\n                                        onBackToChats()\n                                        chatBackOffset.snapTo(0f)\n                                    } else {\n                                        chatBackOffset.animateTo(0f)\n                                    }\n                                    completingBackSwipe = false\n                                    chatSnapshotCapturedForGesture = false\n                                    chatSnapshot = null\n                                    lockedSlidingChatId = null\n                                    lockedSlidingChat = null\n                                    lockedSlidingMessages = null\n                                } else if (allowSidebarSwipe) {",
    )
    source = patch_once(
        source,
        "locked translation",
        "val chatTranslation = if (state.selectedChatId != null) chatBackOffset.value else 0f",
        "val chatTranslation = if (state.selectedChatId != null || lockedSlidingChatId != null) chatBackOffset.value else 0f",
    )
    source = patch_once(
        source,
        "locked snapshot",
        "if (state.selectedChatId != null && chatTranslation > 0f) {",
        "if ((state.selectedChatId != null || lockedSlidingChatId != null) && chatTranslation > 0f) {",
    )
    replacements = [
        ("state = state,\n                                compact = true,\n                                strings = strings,\n                                selectedChat = selectedChat,", "state = chatPaneState,\n                                compact = true,\n                                strings = strings,\n                                selectedChat = chatPaneSelectedChat,"),
        ("state = state,\n                                compact = false,\n                                strings = strings,\n                                selectedChat = selectedChat,", "state = chatPaneState,\n                                compact = false,\n                                strings = strings,\n                                selectedChat = chatPaneSelectedChat,"),
        ("selectedChat?.id?.let {", "chatPaneSelectedChat?.id?.let {"),
        ("onCall = { selectedChat?.id?.let { onCall(it) } },", "onCall = { chatPaneSelectedChat?.id?.let { onCall(it) } },"),
        ("onCancelUpload = { selectedChat?.id?.let { onCancelUpload(it) } },", "onCancelUpload = { chatPaneSelectedChat?.id?.let { onCancelUpload(it) } },"),
        ("${chat.memberIds.size} ${strings.membersCount}", "${formatMembersCount(chat.memberIds.size, strings)}"),
        ("${selectedChat.memberIds.size} ${strings.membersCount}", "${formatMembersCount(selectedChat.memberIds.size, strings)}"),
        ('"${memberCount} members"', "formatMembersCount(memberCount, strings)"),
        ('"${chat.memberIds.size} members"', "formatMembersCount(chat.memberIds.size, strings)"),
        ('"${selectedChat.memberIds.size} members"', "formatMembersCount(selectedChat.memberIds.size, strings)"),
    ]
    for old, new in replacements:
        source = source.replace(old, new)
    return source


def patch_app_view_model(source: str) -> str:
    return patch_once(
        source,
        "optimistic leave navigation",
        "withContext(Dispatchers.IO) { api.leaveChat(session, chatId) }\n                if (_uiState.value.selectedChatId == chatId) {\n                    backToChatList()\n                }\n                refreshHomeSilently()",
        "withContext(Dispatchers.IO) { api.leaveChat(session, chatId) }\n                val current = _uiState.value\n                val updatedChats = current.chats.map { chat ->\n                    if (chat.id == chatId) chat.copy(\n                        memberIds = chat.memberIds.filterNot { it == session.userId },\n                        canChat = false\n                    ) else chat\n                }\n                _uiState.value = current.copy(chats = updatedChats)\n                if (current.selectedChatId == chatId) {\n                    backToChatList()\n                }\n                refreshHomeSilently()",
    )


def main() -> None:
    patch_file("src/main/java/ir/hienob/noveo/ui/Localization.kt", patch_localization)
    patch_file("src/main/java/ir/hienob/noveo/ui/HomeUi.kt", patch_home_ui)
    patch_file("src/main/java/ir/hienob/noveo/app/AppViewModel.kt", patch_app_view_model)


if __name__ == "__main__":
    main()
