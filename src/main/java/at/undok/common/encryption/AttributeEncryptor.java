package at.undok.common.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.spec.KeySpec;
import java.util.Base64;

@Slf4j
@Component
@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final String AES = "AES";

    private static final String INSTANCE = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int LENGTH = 128;

    private final Key key;
    private final Cipher cipher;

    @SneakyThrows
    public AttributeEncryptor(@Value("${undok.secretKey}") String secretString) {
        if (secretString == null || secretString.equals("noKeyProvided") || secretString.getBytes().length != 16) {
            throw new IllegalArgumentException("No valid secretString provided");
        }
        this.key = new SecretKeySpec(secretString.getBytes(), AES);
        this.cipher = Cipher.getInstance(AES);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }

    public String encodeWithUrlEncoder(String toBeEncoded) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getUrlEncoder().encodeToString(cipher.doFinal(toBeEncoded.getBytes()));
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            throw new IllegalStateException(e);
        }
    }

    public String decodeUrlEncoded(String toBeDecoded) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getUrlDecoder().decode(toBeDecoded)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }

    @SneakyThrows
    private SecretKey genKey(String secretString, String salt) {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(INSTANCE);
        KeySpec spec = new PBEKeySpec(secretString.toCharArray(), salt.getBytes(), ITERATIONS, LENGTH);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), AES);
    }

}
