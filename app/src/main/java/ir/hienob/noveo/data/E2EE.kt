package ir.hienob.noveo.data

import android.util.Base64
import java.security.*
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object E2EE {
    private const val EC_ALGORITHM = "EC"
    private const val EC_CURVE = "secp256r1"
    private const val KEY_AGREEMENT_ALGORITHM = "ECDH"
    private const val AES_ALGORITHM = "AES"
    private const val AES_CIPHER_TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12

    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = KeyPairGenerator.getInstance(EC_ALGORITHM)
        keyPairGenerator.initialize(ECGenParameterSpec(EC_CURVE))
        return keyPairGenerator.generateKeyPair()
    }

    fun exportPublicKey(publicKey: PublicKey): String {
        return Base64.encodeToString(publicKey.encoded, Base64.NO_WRAP)
    }

    fun importPublicKey(publicKeyStr: String): PublicKey {
        val keyBytes = Base64.decode(publicKeyStr, Base64.NO_WRAP)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance(EC_ALGORITHM)
        return keyFactory.generatePublic(keySpec)
    }

    fun deriveSharedSecret(privateKey: PrivateKey, publicKey: PublicKey): SecretKeySpec {
        val keyAgreement = KeyAgreement.getInstance(KEY_AGREEMENT_ALGORITHM)
        keyAgreement.init(privateKey)
        keyAgreement.doPhase(publicKey, true)
        val sharedSecret = keyAgreement.generateSecret()

        val messageDigest = MessageDigest.getInstance("SHA-256")
        val aesKeyBytes = messageDigest.digest(sharedSecret)
        return SecretKeySpec(aesKeyBytes, AES_ALGORITHM)
    }

    fun encrypt(aesKey: SecretKeySpec, plaintext: String): EncryptedPayload {
        val cipher = Cipher.getInstance(AES_CIPHER_TRANSFORMATION)
        val iv = ByteArray(GCM_IV_LENGTH)
        SecureRandom().nextBytes(iv)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec)

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
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec)

        val decryptedData = cipher.doFinal(data)
        return String(decryptedData, Charsets.UTF_8)
    }

    fun generateFingerprint(aesKey: SecretKeySpec): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val hash = messageDigest.digest(aesKey.encoded)

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
            val byte1 = hash[i * 2].toInt() and 0xFF
            val byte2 = hash[i * 2 + 1].toInt() and 0xFF
            val value = (byte1 shl 8) or byte2
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
}

data class EncryptedPayload(
    val iv: String,
    val data: String
)

data class E2EESession(
    val sessionId: String,
    val recipientId: String,
    val keyPair: KeyPair,
    var aesKey: SecretKeySpec? = null,
    var status: String = "pending", // "pending", "active"
    var fingerprint: String? = null,
    var isVerified: Boolean = false
)

data class E2EEVerificationSignature(
    val type: String = "e2ee_verification_signature",
    val chatId: String,
    val recipientId: String,
    val sessionId: String,
    val index: Int,
    val emoji: String
)
