package at.undok.common.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
@Component
@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final String AES = "AES";
    private static final String AES_GCM = "AES/GCM/NoPadding";
    private static final String LEGACY_AES_ECB = "AES/ECB/PKCS5Padding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final SecretKey key;
    private final SecureRandom secureRandom = new SecureRandom();

    public AttributeEncryptor(@Value("${undok.secretKey}") String secretString) {
        if (secretString == null || secretString.equals("noKeyProvided") || secretString.getBytes(StandardCharsets.UTF_8).length != 16) {
            throw new IllegalArgumentException("No valid secretString provided");
        }
        this.key = new SecretKeySpec(secretString.getBytes(StandardCharsets.UTF_8), AES);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : Base64.getEncoder().encodeToString(encrypt(attribute));
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return dbData == null ? null : decrypt(Base64.getDecoder().decode(dbData));
    }

    public String encodeWithUrlEncoder(String toBeEncoded) {
        return Base64.getUrlEncoder().encodeToString(encrypt(toBeEncoded));
    }

    public String decodeUrlEncoded(String toBeDecoded) {
        return decrypt(Base64.getUrlDecoder().decode(toBeDecoded));
    }

    private byte[] encrypt(String plaintext) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(AES_GCM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            // Prepend the random IV so it is available for decryption.
            return ByteBuffer.allocate(iv.length + ciphertext.length).put(iv).put(ciphertext).array();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Encryption failed", e);
        }
    }

    private String decrypt(byte[] data) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            byte[] iv = new byte[GCM_IV_LENGTH];
            buffer.get(iv);
            byte[] ciphertext = new byte[buffer.remaining()];
            buffer.get(ciphertext);
            Cipher cipher = Cipher.getInstance(AES_GCM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(ciphertext), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException | RuntimeException e) {
            // Fall back to the legacy AES/ECB scheme so values encrypted before the
            // GCM migration (e.g. reset/confirmation links still in flight) keep working.
            return decryptLegacyEcb(data);
        }
    }

    private String decryptLegacyEcb(byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(LEGACY_AES_ECB);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(data), StandardCharsets.UTF_8);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Decryption failed", e);
        }
    }

}
