package at.undok.it.clienttests.utils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyPair;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ReflectionUtils;

public abstract class KeyUtils {
    private static final String RSA_KEY_HELPER_CLASS = "org.springframework.security.jwt.crypto.sign.RsaKeyHelper";
    private static final String RSA_KEY_HELPER_METHOD = "parseKeyPair";

    @SneakyThrows
    public static Key loadPrivateKey(String pemKeyResource) {
        String keyData = new String(
                FileCopyUtils.copyToByteArray(
                        new ClassPathResource(pemKeyResource).getInputStream()), StandardCharsets.UTF_8);
        Class<?> rsaKeyHelperClass = Class.forName(RSA_KEY_HELPER_CLASS);
        Method parseKeyPairMethod = ReflectionUtils.findMethod(rsaKeyHelperClass, RSA_KEY_HELPER_METHOD, String.class);
        assert parseKeyPairMethod != null;
        ReflectionUtils.makeAccessible(parseKeyPairMethod);
        KeyPair keyPair = (KeyPair)ReflectionUtils.invokeMethod(parseKeyPairMethod, null, keyData);
        assert keyPair != null;
        return keyPair.getPrivate();
    }

}
