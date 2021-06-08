package at.undok.user;

import at.undok.auth.model.entity.User;
import at.undok.auth.service.AuthService;
import at.undok.common.encryption.AttributeEncryptor;
import io.jsonwebtoken.impl.Base64Codec;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

// @TestPropertySource(properties = {"undok.secretKey=abcTestKey"})
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    private static final String USERNAME = "emilius";

    @Autowired
    private AttributeEncryptor attributeEncryptor;

    @Autowired
    private AuthService authService;

    @Test
    public void generateToken() {
        String token = UUID.randomUUID().toString();
        String email = "jens@ist-ur.org";
        String toEncode = email + "," + token;
        String emailEncoded = Base64Codec.BASE64.encode(email);
        String tokenEncoded = Base64Codec.BASE64.encode(token);
        log.info(emailEncoded + " , " + tokenEncoded);
    }

    @Test
    public void encryptUserName() {
        User user = new User();
        String toEncrypted = attributeEncryptor.convertToDatabaseColumn(USERNAME);
        user.setUsername(toEncrypted);
        log.info(user.toString());
        String toDecrypted = attributeEncryptor.convertToEntityAttribute(user.getUsername());
        log.info(toDecrypted);
    }

    @Test
    public void createConfUrlTest() {
        String confirmationUrl = authService.createConfirmationUrl("birgitt@service-b.org", UUID.randomUUID().toString());
        log.info(confirmationUrl);
    }

    @Test
    public void testAttributeEncryptor() {
        String encodedEmail = attributeEncryptor.encodeWithUrlEncoder("birgitt@service-b.org");

        String decodedEmail = attributeEncryptor.decodeUrlEncoded(encodedEmail);

        Assert.assertEquals("birgitt@service-b.org", decodedEmail);
    }

}
