# Plan: Fix Excessive Syncing Issue

## Objective
Stop the app from continuously doing full state resyncs (which consumes ~200KB each time and drains mobile data) by relying purely on real-time WebSocket events. A full state resync should only happen when initially connecting or reconnecting to the server.

## Key Files & Context
- `app/src/main/java/ir/hienob/noveo/app/AppViewModel.kt`: This file currently triggers `refreshHomeSilently()` (which pulls the entire state) in a looping timer and on multiple user actions.

## Implementation Steps
1. **Remove Timer Loop**:
   - Delete the `startSelectedChatRefresh` method completely. This method contains a `while` loop that calls `refreshHomeSilently()` every 5 seconds, causing massive data usage.
   - Remove the call to `startSelectedChatRefresh` inside the `openChat` method.

2. **Remove Unnecessary Syncs**:
   Remove all other calls to `refreshHomeSilently()` that happen on specific actions, as the server already broadcasts real-time changes or we can rely on optimistic updates:
   - Inside `openChat(chatId: String)`.
   - Inside `joinChannel(chatId: String)`.
   - Inside `leaveChat(chatId: String)`.
   - Inside `updateProfile(username: String, bio: String)`.
   - Inside `setLanguage(code: String)`.
   - Inside `handleSocketEvent(event: SocketEvent)` for the following events:
     - `is SocketEvent.ChatUpdated -> {}` (remove `refreshHomeSilently()`)
     - `is SocketEvent.VoiceChatUpdate`
   - Inside `handleIncomingMessage(msg: ChatMessage)` when a chat is not found (let the server send `NewChatInfo` instead).

3. **Retain Reconnect Sync**:
   - The only place `refreshHomeSilently()` will remain is inside `handleSocketEvent` under `is SocketEvent.ConnectionState` when `event.connected` is true. This guarantees the app only pulls the full state when an actual connection or reconnection occurs.

## Verification & Testing
- Build and run the application.
- Monitor network traffic while sitting in an active chat. Verify that the app no longer downloads 200KB chunks every 5 seconds.
- Verify that sending and receiving messages still works instantly via the WebSocket.
- Disconnect and reconnect the network to verify that a full sync is triggered once upon reconnection.