package at.undok.ut;

import at.undok.auth.exception.UserLockedException;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.entity.User;
import at.undok.auth.repository.UserRepo;
import at.undok.auth.service.AuthService;
import at.undok.common.encryption.AttributeEncryptor;
import at.undok.common.util.ToLocalDateService;
import at.undok.it.IntegrationTestBase;
import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.model.enumeration.MaritalStatus;
import at.undok.undok.client.service.ClientService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.Base64Codec;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class UserTest extends IntegrationTestBase {

    private static final String USERNAME = "emilius";

    @Value("${service.b.org.app.jwtSecret}")
    private String jwtSecret;

    @Autowired
    private AttributeEncryptor attributeEncryptor;

    @Autowired
    private AuthService authService;

    @Autowired
    private ClientService clientService;


    @Autowired
    private ToLocalDateService toLocalDateService;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    private static final String HOST = "http://localhost:";

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TestUserBuilder testUserBuilder;

    @BeforeEach
    void setUp() {
        testUserBuilder.createTestUser("emil", "emil@robber.uk", "Pfefferpistole");
    }

    @AfterEach
    void tearDown() {
        userRepo.deleteAll();
        // Don't delete roles as they might be needed by other tests
    }

    @Test
    public void testMist() {
        String forObject = this.testRestTemplate.getForObject(HOST + port + "/service/auth/mist", String.class);
        assertEquals("Hi du verdammter Mistkerl", forObject);
    }

    private String generateJwtToken() {
        String authorities = "";
        return Jwts.builder()
                   .setSubject((USERNAME))
                   .setIssuedAt(new Date())
                   .setExpiration(new Date((new Date()).getTime() + 300000))
                   .claim("roles", authorities)
                   .signWith(SignatureAlgorithm.HS512, jwtSecret)
                   .compact();
    }

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
    public void testAttributeEncryptor() {
        String encodedEmail = attributeEncryptor.encodeWithUrlEncoder("birgitt@service-b.org");
        String decodedEmail = attributeEncryptor.decodeUrlEncoded(encodedEmail);
        assertThat(decodedEmail).isEqualTo("birgitt@service-b.org");
    }

    @Test
    public void testLocalDateFormat()  {
        LocalDate today = LocalDate.now();
        String s = toLocalDateService.localDateToString(today);
        assertEquals(today.toString().substring(8, 10), s.substring(0, 2));
    }

    @Test
    public void testLocalDateTimeFormat()  {
        LocalDateTime now = LocalDateTime.now();
        String s = toLocalDateService.localDateTimeToString(now);
        assertEquals(now.toString().substring(8, 10), s.substring(0, 2));
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
//        assertThat("yes").isEqualTo("no");
    }

    @Test
    public void givenUsingJava8_whenGeneratingRandomAlphanumericString_thenCorrect() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(generatedString);
        System.out.println(generatedString);
    }

    @Test
    public void testAuthServiceGetUserDtoWithSecondFactorJwt() {
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("emil@robber.uk");
        loginDto.setPassword("Pfefferpistole");
        loginDto.setUsername("emil");
        UserDto userDto = authService.getUserDtoWithSecondFactorJwt(loginDto);
        assertNotNull(userDto);
        assertEquals("emil", userDto.getUsername());
    }

    @Test
    void testAdminLogin() {
        User admin = testUserBuilder.createAdminUser();

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("test_admin");
        loginDto.setPassword("admin123");

        UserDto result = authService.getUserDtoWithSecondFactorJwt(loginDto);

        assertTrue(result.isAdmin());
    }

    @Test
    void testLockedUserCannotLogin() {
        User lockedUser = testUserBuilder.createLockedUser();

        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("locked");
        loginDto.setPassword("locked123");

        assertThrows(UserLockedException.class, () ->
                authService.getUserDtoWithSecondFactorJwt(loginDto)
        );
    }

}

