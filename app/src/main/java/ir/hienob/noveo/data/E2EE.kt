package ir.hienob.noveo.data

import android.util.Base64
import org.json.JSONObject
import java.math.BigInteger
import java.security.AlgorithmParameters
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.interfaces.ECPublicKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.ECPublicKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.min

object E2EE {
    private const val EC_ALGORITHM = "EC"
    private const val EC_CURVE_JAVA = "secp256r1"
    private const val EC_CURVE_JWK = "P-256"
    private const val KEY_AGREEMENT_ALGORITHM = "ECDH"
    private const val AES_ALGORITHM = "AES"
    private const val AES_CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12
    private const val P256_COORDINATE_LENGTH = 32
    private val secureRandom = SecureRandom()

    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(EC_ALGORITHM)
        keyPairGenerator.initialize(ECGenParameterSpec(EC_CURVE_JAVA))
        return keyPairGenerator.generateKeyPair()
    }

    fun exportPublicKey(publicKey: PublicKey): JSONObject {
        val ecPublicKey = publicKey as? ECPublicKey
            ?: error("E2EE public key must be an EC public key")
        return JSONObject()
            .put("key_ops", org.json.JSONArray())
            .put("ext", true)
            .put("kty", "EC")
            .put("x", base64UrlEncode(unsignedCoordinate(ecPublicKey.w.affineX)))
            .put("y", base64UrlEncode(unsignedCoordinate(ecPublicKey.w.affineY)))
            .put("crv", EC_CURVE_JWK)
    }

    fun importPublicKey(jwk: JSONObject): PublicKey {
        require(jwk.optString("kty") == "EC") { "Unsupported E2EE key type" }
        require(jwk.optString("crv") == EC_CURVE_JWK) { "Unsupported E2EE curve" }
        val point = ECPoint(
            BigInteger(1, base64UrlDecode(jwk.getString("x"))),
            BigInteger(1, base64UrlDecode(jwk.getString("y")))
        )
        val keySpec = ECPublicKeySpec(point, p256Spec())
        return KeyFactory.getInstance(EC_ALGORITHM).generatePublic(keySpec)
    }

    fun deriveSharedSecret(privateKey: PrivateKey, publicKey: PublicKey): SecretKeySpec {
        val keyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM)
        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)
        val sharedSecret = keyAgreement.generateSecret()

        val aesKeyBytes = when {
            sharedSecret.size == P256_COORDINATE_LENGTH -> sharedSecret
            sharedSecret.size > P256_COORDINATE_LENGTH -> sharedSecret.copyOfRange(sharedSecret.size - P256_COORDINATE_LENGTH, sharedSecret.size)
            else -> ByteArray(P256_COORDINATE_LENGTH).also {
                System.arraycopy(sharedSecret, 0, it, P256_COORDINATE_LENGTH - sharedSecret.size, sharedSecret.size)
            }
        }
        return SecretKeySpec(aesKeyBytes, AES_ALGORITHM)
    }

    fun encrypt(aesKey: SecretKeySpec, plaintext: String): EncryptedPayload {
        val cipher = Cipher.getInstance(AES_CIPHER_TRANSFORMATION)
        val iv = ByteArray(GCM_IV_LENGTH)
        secureRandom.nextBytes(iv)
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        val encryptedData = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        return EncryptedPayload(
            iv = Base64.encodeToString(iv, Base64.NO_WRAP),
            data = Base64.encodeToString(encryptedData, Base64.NO_WRAP)
        )
    }

    fun decrypt(aesKey: SecretKeySpec, payload: EncryptedPayload): String {
        val cipher = Cipher.getInstance(AES_CIPHER_TRANSFORMATION)
        val iv = Base64.decode(payload.iv, Base64.NO_WRAP)
        val data = Base64.decode(payload.data, Base64.NO_WRAP)
        cipher.init(Cipher.DECRYPT_MODE, aesKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return String(cipher.doFinal(data), Charsets.UTF_8)
    }

    fun generateFingerprint(aesKey: SecretKeySpec): String {
        val hash = MessageDigest.getInstance("SHA-256").digest(aesKey.encoded)
        val emojiRanges = listOf(
            0x1F600..0x1F64F,
            0x1F300..0x1F5FF,
            0x1F680..0x1F6FF,
            0x1F900..0x1F9FF,
            0x1FA70..0x1FAFF
        )
        val totalPossible = emojiRanges.sumOf { it.last - it.first + 1 }
        val fingerprint = StringBuilder()
        for (i in 0 until 5) {
            val value = ((hash[i * 2].toInt() and 0xFF) shl 8) or (hash[i * 2 + 1].toInt() and 0xFF)
            var index = value % totalPossible
            var codePoint = 0x1F600
            for (range in emojiRanges) {
                val count = range.last - range.first + 1
                if (index < count) {
                    codePoint = range.first + index
                    break
                }
                index -= count
            }
            fingerprint.append(Character.toChars(codePoint))
        }
        return fingerprint.toString()
    }

    fun encryptedPayloadFromJson(json: JSONObject): EncryptedPayload =
        EncryptedPayload(iv = json.getString("iv"), data = json.getString("data"))

    fun encryptedPayloadToJson(payload: EncryptedPayload): JSONObject =
        JSONObject().put("iv", payload.iv).put("data", payload.data)

    private fun p256Spec(): ECParameterSpec {
        val params = AlgorithmParameters.getInstance(EC_ALGORITHM)
        params.init(ECGenParameterSpec(EC_CURVE_JAVA))
        return params.getParameterSpec(ECParameterSpec::class.java)
    }

    private fun unsignedCoordinate(value: BigInteger): ByteArray {
        val bytes = value.toByteArray()
        val result = ByteArray(P256_COORDINATE_LENGTH)
        val srcPos = if (bytes.size > P256_COORDINATE_LENGTH) bytes.size - P256_COORDINATE_LENGTH else 0
        val copyLength = min(bytes.size, P256_COORDINATE_LENGTH)
        System.arraycopy(bytes, srcPos, result, P256_COORDINATE_LENGTH - copyLength, copyLength)
        return result
    }

    private fun base64UrlEncode(bytes: ByteArray): String =
        Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)

    private fun base64UrlDecode(value: String): ByteArray =
        Base64.decode(value, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
}

class E2EEManager {
    private val sessions = linkedMapOf<String, E2EESession>()
    private val aliases = linkedMapOf<String, String>()

    @Synchronized
    fun snapshot(): Map<String, E2EESessionSnapshot> = sessions.mapValues { (_, session) ->
        E2EESessionSnapshot(
            sessionId = session.sessionId,
            recipientId = session.recipientId,
            chatId = session.chatId,
            status = session.status,
            fingerprint = session.fingerprint,
            verified = session.verified
        )
    } + aliases.mapNotNull { (alias, canonical) ->
        sessions[canonical]?.let { session ->
            alias to E2EESessionSnapshot(
                sessionId = session.sessionId,
                recipientId = session.recipientId,
                chatId = session.chatId,
                status = session.status,
                fingerprint = session.fingerprint,
                verified = session.verified
            )
        }
    }.toMap()

    @Synchronized
    fun getSession(chatId: String): E2EESession? = sessions[resolveChatId(chatId)]

    @Synchronized
    fun isActive(chatId: String): Boolean = getSession(chatId)?.status == E2EESessionStatus.ACTIVE

    @Synchronized
    fun clear() {
        sessions.clear()
        aliases.clear()
    }

    fun canonicalPrivateChatId(selfUserId: String, peerId: String): String =
        listOf(selfUserId, peerId).sorted().joinToString("_")

    @Synchronized
    fun connectE2EE(selfUserId: String, chatId: String, recipientId: String): JSONObject {
        val canonicalChatId = canonicalPrivateChatId(selfUserId, recipientId).ifBlank { chatId }
        val sessionId = "e2ee-${System.currentTimeMillis()}-${randomSuffix()}"
        val keyPair = E2EE.generateKeyPair()
        if (chatId != canonicalChatId) aliases[chatId] = canonicalChatId
        sessions[canonicalChatId] = E2EESession(
            sessionId = sessionId,
            chatId = canonicalChatId,
            recipientId = recipientId,
            keyPair = keyPair,
            status = E2EESessionStatus.PENDING
        )
        return JSONObject()
            .put("type", "e2ee_session_request")
            .put("chatId", canonicalChatId)
            .put("recipientId", recipientId)
            .put("sessionId", sessionId)
            .put("publicKey", E2EE.exportPublicKey(keyPair.public))
    }

    @Synchronized
    fun acceptSessionRequest(selfUserId: String, data: JSONObject): E2EEHandshakeResult? {
        val peerId = data.optString("senderId").ifBlank { data.optString("recipientId") }
        if (peerId.isBlank()) return null
        val chatId = data.optString("chatId").ifBlank { canonicalPrivateChatId(selfUserId, peerId) }
        val sessionId = data.optString("sessionId")
        val peerPublicKeyJson = data.optJSONObject("publicKey") ?: return null
        val keyPair = E2EE.generateKeyPair()
        val aesKey = E2EE.deriveSharedSecret(keyPair.private, E2EE.importPublicKey(peerPublicKeyJson))
        val fingerprint = E2EE.generateFingerprint(aesKey)
        sessions[chatId] = E2EESession(
            sessionId = sessionId,
            chatId = chatId,
            recipientId = peerId,
            keyPair = keyPair,
            aesKey = aesKey,
            status = E2EESessionStatus.ACTIVE,
            fingerprint = fingerprint,
            verified = false
        )
        val acceptPayload = JSONObject()
            .put("type", "e2ee_session_accept")
            .put("chatId", chatId)
            .put("recipientId", peerId)
            .put("sessionId", sessionId)
            .put("publicKey", E2EE.exportPublicKey(keyPair.public))
        return E2EEHandshakeResult(
            chatId = chatId,
            session = sessions.getValue(chatId),
            outbound = listOf(acceptPayload, createVerificationPayload(chatId))
        )
    }

    @Synchronized
    fun finishSessionAccept(data: JSONObject): E2EEHandshakeResult? {
        val chatId = resolveChatId(data.optString("chatId"))
        val session = sessions[chatId] ?: return null
        if (session.sessionId != data.optString("sessionId")) return null
        val peerPublicKeyJson = data.optJSONObject("publicKey") ?: return null
        val aesKey = E2EE.deriveSharedSecret(session.keyPair.private, E2EE.importPublicKey(peerPublicKeyJson))
        session.aesKey = aesKey
        session.fingerprint = E2EE.generateFingerprint(aesKey)
        session.verified = false
        session.status = E2EESessionStatus.ACTIVE
        return E2EEHandshakeResult(
            chatId = chatId,
            session = session,
            outbound = listOf(createVerificationPayload(chatId))
        )
    }

    @Synchronized
    fun endE2EE(chatId: String, notifyPeer: Boolean = true): JSONObject? {
        val canonicalChatId = resolveChatId(chatId)
        val session = sessions.remove(canonicalChatId) ?: return null
        aliases.entries.removeAll { it.value == canonicalChatId || it.key == chatId }
        if (!notifyPeer) return null
        return JSONObject()
            .put("type", "e2ee_session_end")
            .put("chatId", canonicalChatId)
            .put("recipientId", session.recipientId)
            .put("sessionId", session.sessionId)
    }

    @Synchronized
    fun handleSessionEnd(data: JSONObject): String? {
        val chatId = data.optString("chatId")
        if (chatId.isBlank()) return null
        val canonicalChatId = resolveChatId(chatId)
        sessions.remove(canonicalChatId)
        aliases.entries.removeAll { it.value == canonicalChatId || it.key == chatId }
        return chatId
    }

    @Synchronized
    fun handleVerificationSignature(data: JSONObject): E2EEVerificationResult? {
        val chatId = resolveChatId(data.optString("chatId"))
        val session = sessions[chatId] ?: return null
        if (session.status != E2EESessionStatus.ACTIVE || session.sessionId != data.optString("sessionId")) return null
        val emojis = splitCodePoints(session.fingerprint.orEmpty())
        val index = data.optInt("index", -1)
        val matches = index in emojis.indices && emojis[index] == data.optString("emoji")
        if (matches) {
            session.verified = true
            return E2EEVerificationResult(chatId, verified = true, shouldEndSession = false)
        }
        sessions.remove(chatId)
        aliases.entries.removeAll { it.value == chatId }
        return E2EEVerificationResult(chatId, verified = false, shouldEndSession = true)
    }

    @Synchronized
    fun sendE2EE(
        selfUserId: String,
        selfName: String,
        chatId: String,
        plaintext: String,
        includeSessionId: Boolean = false
    ): E2EEOutgoingMessage {
        val canonicalChatId = resolveChatId(chatId)
        val session = sessions[canonicalChatId] ?: error("No E2EE session for chat $chatId")
        require(session.status == E2EESessionStatus.ACTIVE) { "E2EE session is not active" }
        val aesKey = session.aesKey ?: error("No E2EE key for chat $chatId")
        val encryptedPayload = E2EE.encrypt(aesKey, plaintext)
        val messageId = "e2ee-${System.currentTimeMillis()}-${randomSuffix()}"
        val timestamp = System.currentTimeMillis() / 1000
        val payload = JSONObject()
            .put("type", "e2ee_message")
            .put("chatId", canonicalChatId)
            .put("recipientId", session.recipientId)
            .put("messageId", messageId)
            .put("timestamp", timestamp)
            .put("encryptedPayload", E2EE.encryptedPayloadToJson(encryptedPayload))
        if (includeSessionId) payload.put("sessionId", session.sessionId)
        return E2EEOutgoingMessage(
            payload = payload,
            localMessage = ChatMessage(
                id = messageId,
                chatId = chatId,
                senderId = selfUserId,
                senderName = selfName,
                content = parseMessageContent(if (plaintext.trim().startsWith("{")) plaintext else JSONObject().put("text", plaintext)),
                timestamp = timestamp,
                pending = false,
                e2ee = true
            )
        )
    }

    @Synchronized
    fun decryptIncomingMessage(
        data: JSONObject,
        selfUserId: String,
        usersById: Map<String, UserSummary>
    ): ChatMessage? {
        val senderId = data.optString("senderId")
        val recipientId = data.optString("recipientId")
        val peerId = if (senderId == selfUserId) recipientId else senderId
        val wireChatId = data.optString("chatId").ifBlank { canonicalPrivateChatId(selfUserId, peerId) }
        val chatId = resolveChatId(wireChatId)
        val localChatId = aliases.entries.firstOrNull { it.value == chatId }?.key ?: wireChatId
        val session = sessions[chatId] ?: return null
        val aesKey = session.aesKey ?: return null
        val encryptedPayload = data.optJSONObject("encryptedPayload") ?: return null
        val decrypted = E2EE.decrypt(aesKey, E2EE.encryptedPayloadFromJson(encryptedPayload))
        val content = parseMessageContent(decrypted)
        return ChatMessage(
            id = data.optString("messageId").ifBlank { "e2ee-${System.currentTimeMillis()}-${randomSuffix()}" },
            chatId = localChatId,
            senderId = senderId,
            senderName = usersById[senderId]?.username ?: "User",
            chatType = "private",
            content = content,
            timestamp = data.optLong("timestamp", System.currentTimeMillis() / 1000),
            seenBy = emptyList(),
            pending = false,
            e2ee = true
        )
    }

    private fun createVerificationPayload(chatId: String): JSONObject {
        val canonicalChatId = resolveChatId(chatId)
        val session = sessions.getValue(canonicalChatId)
        val emojis = splitCodePoints(session.fingerprint.orEmpty())
        val index = if (emojis.isEmpty()) 0 else secureIndex(emojis.size)
        val emoji = emojis.getOrElse(index) { "" }
        return JSONObject()
            .put("type", "e2ee_verification_signature")
            .put("chatId", canonicalChatId)
            .put("recipientId", session.recipientId)
            .put("sessionId", session.sessionId)
            .put("index", index)
            .put("emoji", emoji)
    }

    private fun randomSuffix(): String {
        val alphabet = "abcdefghijklmnopqrstuvwxyz0123456789"
        return buildString {
            repeat(6) {
                append(alphabet[secureIndex(alphabet.length)])
            }
        }
    }

    private fun secureIndex(bound: Int): Int = SecureRandom().nextInt(bound)

    private fun resolveChatId(chatId: String): String = aliases[chatId] ?: chatId

    private fun splitCodePoints(value: String): List<String> {
        val result = mutableListOf<String>()
        var index = 0
        while (index < value.length) {
            val codePoint = Character.codePointAt(value, index)
            result.add(String(Character.toChars(codePoint)))
            index += Character.charCount(codePoint)
        }
        return result
    }
}

enum class E2EESessionStatus {
    PENDING,
    ACTIVE
}

data class EncryptedPayload(
    val iv: String,
    val data: String
)

data class E2EESession(
    val sessionId: String,
    val chatId: String,
    val recipientId: String,
    val keyPair: KeyPair,
    var aesKey: SecretKeySpec? = null,
    var status: E2EESessionStatus = E2EESessionStatus.PENDING,
    var fingerprint: String? = null,
    var verified: Boolean = false
)

data class E2EESessionSnapshot(
    val sessionId: String,
    val recipientId: String,
    val chatId: String,
    val status: E2EESessionStatus,
    val fingerprint: String?,
    val verified: Boolean
)

data class E2EEHandshakeResult(
    val chatId: String,
    val session: E2EESession,
    val outbound: List<JSONObject>
)

data class E2EEVerificationResult(
    val chatId: String,
    val verified: Boolean,
    val shouldEndSession: Boolean
)

data class E2EEOutgoingMessage(
    val payload: JSONObject,
    val localMessage: ChatMessage
)

data class E2EEVerificationSignature(
    val type: String = "e2ee_verification_signature",
    val chatId: String,
    val recipientId: String,
    val sessionId: String,
    val index: Int,
    val emoji: String
)
