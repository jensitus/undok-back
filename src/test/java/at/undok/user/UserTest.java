package at.undok.user;

import at.undok.auth.model.entity.User;
import at.undok.auth.service.AuthService;
import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.util.UUIDConverter;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Address;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.enumeration.MaritalStatus;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.repository.AddressRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.PersonRepo;
import at.undok.undok.client.service.ClientService;
import at.undok.undok.client.service.CounselingService;
import io.jsonwebtoken.impl.Base64Codec;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    @Autowired
    private AddressRepo addressRepo;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private ClientService clientService;

    @Autowired
    private PersonRepo personRepo;

    @Autowired
    private UUIDConverter uuidConverter;

    @Autowired
    private CounselingService counselingService;

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

    @Test
    public void testAddressRepo() {
        Address address = new Address();
        address.setCity("Vienna");
        address.setCountry("Austria");
        address.setZipCode("1200");
        address.setStreet("Klosterneuburger");
        Address address1 = addressRepo.save(address);
        Assert.assertEquals(address1.getCity(), address.getCity());
    }

    @Test
    public void testClientRepo() {

        ClientForm clientForm = new ClientForm();
        clientForm.setFirstName("Emil");
        clientForm.setLastName("Donner");
        clientForm.setEducation("yes");
        clientForm.setKeyword("Kette");
        clientForm.setVulnerableWhenAssertingRights(Boolean.FALSE);
        clientForm.setInterpreterNecessary(Boolean.TRUE);
        clientForm.setHowHasThePersonHeardFromUs("Freunde");

        ClientDto expected = clientService.createClient(clientForm);

        Assert.assertEquals(expected.getEducation(), clientForm.getEducation());
    }

    @Test
    public void getPerson() {
        Person p = personRepo.findByFirstName("Emil");
        // Person p = personRepo.getOne(uuidConverter.convertToEntityAttribute("90242ff4-1547-4d7a-880d-8a4731a2c9e0"));
        Assert.assertEquals(p.getId(), UUID.fromString("90242ff4-1547-4d7a-880d-8a4731a2c9e0"));
    }

    @Test
    public void getClient() {
        // Client c = clientRepo.getOne(UUID.fromString("15c59593-20bf-4cc4-afd3-1d7878ee0770"));
        List<Client> c = clientRepo.findByKeyword("Kette");
        for (Client client : c) {
            Assert.assertEquals(client.getKeyword(), "Kette");
        }

    }

    @Test
    public void testToEnum() {
        Client client = new Client();
        client.setMaritalStatus("Verheiratet");
        Assert.assertEquals(client.getMaritalStatus(), MaritalStatus.MARRIED);
    }

    @Test
    public void testTimeParse() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("16-07-2021 13:30:11", dateTimeFormatter);
        Assert.assertEquals("yes", "no");
    }

    @Test
    public void testGetFutureCounselings() {
        List<CounselingDto> futureCounselings = counselingService.getFutureCounselings();
        Assert.assertEquals("yes", "no");
    }

    @Test
    public void testGetPastCounselings() {
        List<CounselingDto> futureCounselings = counselingService.getPastCounselings();
        Assert.assertEquals("yes", "no");
    }

}
