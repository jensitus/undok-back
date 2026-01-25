package at.undok.it.cucumber.auth;

import at.undok.auth.message.JwtResponse;
import at.undok.auth.message.PasswordResetForm;
import at.undok.auth.model.dto.*;
import at.undok.auth.model.form.ConfirmAccountForm;
import at.undok.auth.model.form.CreateUserForm;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.common.message.Message;
import org.springframework.security.core.userdetails.UserDetails;
import at.undok.undok.client.model.dto.AllClientDto;
import at.undok.undok.client.model.dto.AllCounselingDto;
import at.undok.undok.client.model.dto.ClientDto;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.form.ClientForm;
import at.undok.undok.client.model.form.CounselingForm;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AuthRestApiClient {

    private static final String HOST = "http://localhost:";
    private static final String UNDOK_CLIENTS_PATH = "/service/undok/clients";
    private static final String UNDOK_COUNSELINGS_PATH = "/service/undok/counselings";
    private final TestRestTemplate testRestTemplate;
    private final Integer serverPort;

    public AuthRestApiClient(TestRestTemplate testRestTemplate, int serverPort) {
        this.testRestTemplate = testRestTemplate;
        this.serverPort = serverPort;
    }

    public ResponseEntity<Message> registerUser(SignUpDto signUpDto) {
        return testRestTemplate.postForEntity(
                "http://localhost:{port}/service/auth/signup",
                signUpDto,
                Message.class,
                Map.of("port", serverPort)
        );
    }

    public ResponseEntity<JwtResponse> login(LoginDto loginDto) {
        String url = "http://localhost:{port}//service/auth/login";
        return testRestTemplate.postForEntity(url, loginDto, JwtResponse.class, Map.of("port", serverPort));
    }

    public ResponseEntity<JwtResponse> secFac(SecondFactorForm secondFactorForm, String token) {
        String url = HOST + serverPort + "/service/second-factor-auth/second-factor";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);
        HttpEntity<SecondFactorForm> secondFactorFormHttpEntity = new HttpEntity<>(secondFactorForm, httpHeaders);
        return testRestTemplate.postForEntity(url, secondFactorFormHttpEntity, JwtResponse.class);
    }

    public ResponseEntity<ClientDto> createClient(ClientForm clientForm, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + UNDOK_CLIENTS_PATH + "/create";
        HttpEntity<ClientForm> clientFormHttpEntity = new HttpEntity<>(clientForm, httpHeaders);
        ResponseEntity<ClientDto> clientDtoResponseEntity = this.testRestTemplate.postForEntity(url, clientFormHttpEntity, ClientDto.class);

        return clientDtoResponseEntity;
    }

    public ResponseEntity<CounselingDto> createCounseling(CounselingForm counselingForm, String accessToken, UUID clientId) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + UNDOK_CLIENTS_PATH + "/" + clientId + "/counseling";
        HttpEntity<CounselingForm> counselingFormHttpEntity = new HttpEntity<>(counselingForm, httpHeaders);
        return testRestTemplate.postForEntity(url, counselingFormHttpEntity, CounselingDto.class);
    }

    public ResponseEntity<List<AllCounselingDto>> getAllCounselings(String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + UNDOK_COUNSELINGS_PATH + "/all";
        HttpEntity entity = new HttpEntity(httpHeaders);
        ResponseEntity<List<AllCounselingDto>> response = testRestTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<AllCounselingDto>>() {
        });

        return response;
    }

    public ClientDto getClient(UUID clientId, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + UNDOK_CLIENTS_PATH + "/" + clientId;
        HttpEntity entity = new HttpEntity<>(httpHeaders);
        return testRestTemplate.getForObject(url, ClientDto.class);
    }

    public List<ClientDto> getAllClients(String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + UNDOK_CLIENTS_PATH + "/all/active";
        HttpEntity entity = new HttpEntity<>(httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<ClientDto>>() {}).getBody();
    }

    public void deleteClient(UUID clientId, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + UNDOK_CLIENTS_PATH + "/" + clientId + "/set-deleted";
        HttpEntity entity = new HttpEntity<>(httpHeaders);
        testRestTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    private HttpHeaders getHeaders(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        return httpHeaders;
    }

    public ResponseEntity<Message> pingPong(String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/undok/ping/pong";
        HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Message> response = testRestTemplate.exchange(url, HttpMethod.GET, entity, Message.class);
        return response;
    }

    public ResponseEntity<Message> resetPassword(PasswordResetForm passwordResetForm) {
        String url = HOST + serverPort + "/service/auth/reset_password";
        return testRestTemplate.postForEntity(url, passwordResetForm, Message.class);
    }

    public ResponseEntity<String> validatePasswordResetToken(String token, String email) {
        String url = HOST + serverPort + "/service/auth/reset_password/" + token + "/edit?email=" + email;
        return testRestTemplate.getForEntity(url, String.class);
    }

    public ResponseEntity<Message> resetPasswordWithToken(PasswordResetForm passwordResetForm, String token, String email) {
        String url = HOST + serverPort + "/service/auth/reset_password/" + token + "?email=" + email;
        HttpEntity<PasswordResetForm> requestEntity = new HttpEntity<>(passwordResetForm);
        return testRestTemplate.exchange(url, HttpMethod.PUT, requestEntity, Message.class);
    }

    public ResponseEntity<Message> checkConfirmationData(String token, String confirm, String encodedEmail) {
        String url = HOST + serverPort + "/service/auth/" + token + "/" + confirm + "/" + encodedEmail;
        return testRestTemplate.getForEntity(url, Message.class);
    }

    public ResponseEntity<Message> setNewPassword(ConfirmAccountForm confirmAccountForm) {
        String url = HOST + serverPort + "/service/auth/" + confirmAccountForm.getConfirmationToken() + "/set_new_password";
        return testRestTemplate.postForEntity(url, confirmAccountForm, Message.class);
    }

    // User API methods

    public ResponseEntity<List> getAllUsers(String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/all";
        HttpEntity entity = new HttpEntity(httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, List.class);
    }

    public ResponseEntity<UserDto> getUserByUsername(String username, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/by_username/" + username;
        HttpEntity entity = new HttpEntity(httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, UserDto.class);
    }

    public ResponseEntity<UserDetails> getSpecialUser(String username, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/principle/" + username;
        HttpEntity entity = new HttpEntity(httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, UserDetails.class);
    }

    public ResponseEntity<String> getSpecialUserAsString(String username, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/principle/" + username;
        HttpEntity entity = new HttpEntity(httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    }

    public ResponseEntity<Message> checkAuthToken(String token, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/auth/check_auth_token";
        HttpEntity<String> entity = new HttpEntity<>(token, httpHeaders);
        return testRestTemplate.postForEntity(url, entity, Message.class);
    }

    public ResponseEntity<Message> requestPasswordReset(String email, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/auth/password_resets";
        HttpEntity<String> entity = new HttpEntity<>(email, httpHeaders);
        return testRestTemplate.postForEntity(url, entity, Message.class);
    }

    public ResponseEntity<Message> changePassword(ChangePwDto changePwDto, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/changepw";
        HttpEntity<ChangePwDto> entity = new HttpEntity<>(changePwDto, httpHeaders);
        return testRestTemplate.postForEntity(url, entity, Message.class);
    }

    public ResponseEntity<Message> setAdminFlag(UUID userId, boolean isAdmin, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/set-admin/" + userId;
        SetAdminDto setAdminDto = new SetAdminDto();
        setAdminDto.setAdmin(isAdmin);
        HttpEntity<SetAdminDto> entity = new HttpEntity<>(setAdminDto, httpHeaders);
        return testRestTemplate.postForEntity(url, entity, Message.class);
    }

    public ResponseEntity<Message> createUserViaAdmin(CreateUserForm createUserForm, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/create-user-via-admin";
        HttpEntity<CreateUserForm> entity = new HttpEntity<>(createUserForm, httpHeaders);
        return testRestTemplate.postForEntity(url, entity, Message.class);
    }

    public ResponseEntity<Message> resendConfirmationLink(String userId, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/resend-confirmation-link";
        HttpEntity<String> entity = new HttpEntity<>(userId, httpHeaders);
        return testRestTemplate.postForEntity(url, entity, Message.class);
    }

    public ResponseEntity<Message> lockUser(UUID userId, boolean lock, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + "/service/users/" + userId + "/lock";
        LockUserDto lockUserDto = new LockUserDto();
        lockUserDto.setId(userId);
        lockUserDto.setLock(lock);
        HttpEntity<LockUserDto> entity = new HttpEntity<>(lockUserDto, httpHeaders);
        return testRestTemplate.postForEntity(url, entity, Message.class);
    }

}
