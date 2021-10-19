package at.undok.ut;

import at.undok.auth.model.entity.User;
import at.undok.auth.service.AuthService;
import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.util.UUIDConverter;
import at.undok.undok.client.model.dto.AllClientDto;
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
import at.undok.undok.client.service.CsvService;
import io.jsonwebtoken.impl.Base64Codec;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

// @TestPropertySource(properties = {"undok.secretKey=abcTestKey"})
@Slf4j
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

    @Autowired
    private CsvService csvService;

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
        // String confirmationUrl = authService.createConfirmationUrl("birgitt@service-b.org", UUID.randomUUID().toString());
        // log.info(confirmationUrl);
    }

    @Test
    public void testAttributeEncryptor() {
        String encodedEmail = attributeEncryptor.encodeWithUrlEncoder("birgitt@service-b.org");

        String decodedEmail = attributeEncryptor.decodeUrlEncoded(encodedEmail);

        assertThat(decodedEmail).isEqualTo("birgitt@service-b.org");
    }

    @Test
    public void testAddressRepo() {
        Address address = new Address();
        address.setCity("Vienna");
        address.setCountry("Austria");
        address.setZipCode("1200");
        address.setStreet("Klosterneuburger");
        Address address1 = addressRepo.save(address);
        assertThat(address1).isEqualTo(address);
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

        assertThat(expected.getEducation()).isEqualTo(clientForm.getEducation());
    }

    @Test
    public void testCsv() throws IOException {
        csvService.writeClientsToCsv();
    }

    @Test
    public void findKeywords() {
        List<AllClientDto> all = clientService.getAll();
        List<String> countryNames = new ArrayList<>();
        List<Person> clientPersons = new ArrayList<>();
        for (AllClientDto clientDto : all) {
            countryNames.add(clientDto.getCountry());
        }

        List<String> countryNamesSorted = new ArrayList<>();
        for (String countryName : countryNames) {
            if (!countryNamesSorted.contains(countryName)) {
                countryNamesSorted.add(countryName);
            }
        }
        List<Integer> countryNumbers = new ArrayList<>();
        for (String cName : countryNamesSorted) {
            countryNumbers.add(countCountryNames(cName, countryNames));
        }

        log.info(countryNamesSorted.toString());
        log.info(countryNumbers.toString());
    }

    private int countCountryNames(String countryName, List<String> countryNames) {
        int countryCounter = 0;
        List<Integer> countryNumbers = new ArrayList<>();
        for (String cName : countryNames) {
            if (cName == null && countryName == null) {
                countryCounter = countryCounter + 1;
            } else if (cName != null && countryName != null) {
                if (cName.equals(countryName)) {
                    countryCounter = countryCounter + 1;
                }
            } else {

            }
        }
        return countryCounter;
    }

    @Test
    public void getPerson() {
        Person p = personRepo.findByFirstName("Emil");
        // Person p = personRepo.getOne(uuidConverter.convertToEntityAttribute("90242ff4-1547-4d7a-880d-8a4731a2c9e0"));
        
        assertThat(p.getId()).isEqualTo(UUID.fromString("90242ff4-1547-4d7a-880d-8a4731a2c9e0"));
    }

    @Test
    public void getClient() {
        // Client c = clientRepo.getOne(UUID.fromString("15c59593-20bf-4cc4-afd3-1d7878ee0770"));
        List<Client> c = clientRepo.findByKeyword("Kette");
        for (Client client : c) {
            assertThat(client.getKeyword()).isEqualTo("Kette");
        }

    }

    @Test
    public void testToEnum() {
        Client client = new Client();
        client.setMaritalStatus("Verheiratet");
        assertThat(client.getMaritalStatus()).isEqualTo(MaritalStatus.MARRIED.label);
    }

    @Test
    public void testTimeParse() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.parse("16-07-2021 13:30:11", dateTimeFormatter);
        assertThat("yes").isEqualTo("no");
    }

    @Test
    public void testGetFutureCounselings() {
        List<CounselingDto> futureCounselings = counselingService.getFutureCounselings();
        assertThat("yes").isEqualTo("no");
    }

    @Test
    public void testGetPastCounselings() {
        List<CounselingDto> futureCounselings = counselingService.getPastCounselings();
        assertThat("yes").isEqualTo("no");
    }

}
