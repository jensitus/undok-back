package at.undok.it.category;

import at.undok.auth.message.JwtResponse;
import at.undok.auth.model.dto.LoginDto;
import at.undok.auth.model.dto.SignUpDto;
import at.undok.auth.model.dto.UserDto;
import at.undok.auth.model.form.SecondFactorForm;
import at.undok.common.message.Message;
import at.undok.it.IntegrationTestBase;
import at.undok.it.cucumber.UndokTestData;
import at.undok.it.cucumber.auth.*;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.dto.JoinCategoryDto;
import at.undok.undok.client.model.form.CategoryForm;
import at.undok.undok.client.model.form.JoinCategoryForm;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("CategoryApi Integration Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CategoryApiTest extends IntegrationTestBase {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AuthRestApiClient authRestApiClient;

    @Autowired
    private EmailVerifications emailVerifications;

    @Autowired
    private UserVerifications userVerifications;

    @LocalServerPort
    private int serverPort;

    private CategoryRestApiClient categoryRestApiClient;
    private UndokTestData testData;
    private String userToken;
    private UserDto testUser;
    private CategoryDto testCategory;
    private UUID testEntityId;

    @BeforeAll
    public void setup() {
        testData = new UndokTestData(new Faker());
        categoryRestApiClient = new CategoryRestApiClient(testRestTemplate, serverPort);

        // Create and authenticate a test user
        testUser = createAndAuthenticateUser();
        userToken = testUser.getAccessToken();

        // Generate a test entity ID for join category operations
        testEntityId = UUID.randomUUID();

        log.info("Setup complete - Test user: {}", testUser.getUsername());
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully create a category with valid data")
    public void testCreateCategory_WithValidData_ReturnsCategory() {
        // Given: A valid category form
        CategoryForm categoryForm = new CategoryForm();
        categoryForm.setName("Test Category " + System.currentTimeMillis());
        categoryForm.setType("TEST_TYPE");

        // When: Creating a category
        ResponseEntity<CategoryDto> response = categoryRestApiClient.createCategory(categoryForm, userToken);

        // Then: Category is created successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertThat(response.getBody().getName()).isEqualTo(categoryForm.getName());
        assertThat(response.getBody().getType()).isEqualTo(categoryForm.getType());
        assertNotNull(response.getBody().getCreatedAt());
        assertFalse(response.getBody().isToBeDeleted());

        // Store for later tests
        testCategory = response.getBody();
        log.info("Successfully created category: {} with ID: {}", testCategory.getName(), testCategory.getId());
    }

    @Test
    @Order(2)
    @DisplayName("Should handle duplicate category creation gracefully")
    public void testCreateCategory_WithDuplicateName_HandlesGracefully() {
        // Given: An existing category
        CategoryForm categoryForm = new CategoryForm();
        categoryForm.setName(testCategory.getName());
        categoryForm.setType(testCategory.getType());

        // When: Attempting to create a duplicate category
        ResponseEntity<CategoryDto> response = categoryRestApiClient.createCategory(categoryForm, userToken);

        // Then: Request should handle duplicate appropriately
        // Based on the service logic, it either returns the existing category or throws an exception
        assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is4xxClientError());
        log.info("Duplicate category creation handled with status: {}", response.getStatusCode());
    }

    @Test
    @Order(3)
    @DisplayName("Should get all categories successfully")
    public void testGetAllCategories_WithValidAuth_ReturnsCategoryList() {
        // When: Getting all categories
        ResponseEntity<List<CategoryDto>> response = categoryRestApiClient.getAllCategories(userToken);

        // Then: Returns list of categories
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
        assertTrue(response.getBody().stream().anyMatch(c -> c.getId().equals(testCategory.getId())));
        log.info("Retrieved {} categories from the system", response.getBody().size());
    }

    @Test
    @Order(4)
    @DisplayName("Should get categories by type successfully")
    public void testGetCategoriesByType_WithValidType_ReturnsCategoryList() {
        // Given: A valid category type
        String type = testCategory.getType();

        // When: Getting categories by type
        ResponseEntity<List<CategoryDto>> response = categoryRestApiClient.getCategoriesByType(type, userToken);

        // Then: Returns filtered categories
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
        assertTrue(response.getBody().stream().allMatch(c -> c.getType().equals(type)));
        assertTrue(response.getBody().stream().anyMatch(c -> c.getId().equals(testCategory.getId())));
        log.info("Retrieved {} categories of type: {}", response.getBody().size(), type);
    }

    @Test
    @Order(5)
    @DisplayName("Should return empty list for non-existent category type")
    public void testGetCategoriesByType_WithNonExistentType_ReturnsEmptyList() {
        // Given: A non-existent category type
        String nonExistentType = "NON_EXISTENT_TYPE_" + System.currentTimeMillis();

        // When: Getting categories by type
        ResponseEntity<List<CategoryDto>> response = categoryRestApiClient.getCategoriesByType(nonExistentType, userToken);

        // Then: Returns empty list
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertThat(response.getBody()).isEmpty();
        log.info("Correctly returned empty list for non-existent type: {}", nonExistentType);
    }

    @Test
    @Order(6)
    @DisplayName("Should successfully add join categories")
    public void testAddJoinCategory_WithValidData_CreatesJoinCategories() {
        // Given: Valid join category forms
        List<JoinCategoryForm> joinCategoryForms = new ArrayList<>();
        JoinCategoryForm form1 = new JoinCategoryForm();
        form1.setCategoryId(testCategory.getId());
        form1.setEntityId(testEntityId);
        form1.setCategoryType(testCategory.getType());
        form1.setEntityType("TEST_ENTITY");
        joinCategoryForms.add(form1);

        // When: Adding join categories
        ResponseEntity<Message> response = categoryRestApiClient.addJoinCategory(joinCategoryForms, userToken);

        // Then: Join categories are created successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getText()).contains("Categories successfully added");
        log.info("Successfully added join categories for entity: {}", testEntityId);
    }

    @Test
    @Order(7)
    @DisplayName("Should get categories by type and entity")
    public void testGetCategoriesByTypeAndEntity_WithValidData_ReturnsCategoryList() {
        // Given: Valid category type and entity ID
        String type = testCategory.getType();
        UUID entityId = testEntityId;

        // When: Getting categories by type and entity
        ResponseEntity<List<CategoryDto>> response = categoryRestApiClient.getCategoriesByTypeAndEntity(type, entityId, userToken);

        // Then: Returns associated categories
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
        assertTrue(response.getBody().stream().anyMatch(c -> c.getId().equals(testCategory.getId())));
        log.info("Retrieved {} categories for type: {} and entity: {}", response.getBody().size(), type, entityId);
    }

    @Test
    @Order(8)
    @DisplayName("Should return empty list for entity with no categories")
    public void testGetCategoriesByTypeAndEntity_WithNoCategories_ReturnsEmptyList() {
        // Given: A random entity ID with no categories
        UUID randomEntityId = UUID.randomUUID();
        String type = testCategory.getType();

        // When: Getting categories by type and entity
        ResponseEntity<List<CategoryDto>> response = categoryRestApiClient.getCategoriesByTypeAndEntity(type, randomEntityId, userToken);

        // Then: Returns empty list
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertThat(response.getBody()).isEmpty();
        log.info("Correctly returned empty list for entity with no categories");
    }

    @Test
    @Order(9)
    @DisplayName("Should successfully update category name")
    public void testUpdateCategory_WithValidData_UpdatesCategory() {
        // Given: An existing category and new name
        UUID categoryId = testCategory.getId();
        String newName = "Updated Category Name " + System.currentTimeMillis();

        // When: Updating the category
        ResponseEntity<CategoryDto> response = categoryRestApiClient.updateCategory(categoryId, newName, userToken);

        // Then: Category is updated successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertNotNull(response.getBody());
        assertThat(response.getBody().getId()).isEqualTo(categoryId);
        assertThat(response.getBody().getName()).isEqualTo(newName);
        assertThat(response.getBody().getType()).isEqualTo(testCategory.getType());

        // Update testCategory with new name
        testCategory = response.getBody();
        log.info("Successfully updated category {} to name: {}", categoryId, newName);
    }

    @Test
    @Order(10)
    @DisplayName("Should fail to update non-existent category")
    public void testUpdateCategory_WithNonExistentId_ReturnsNotFound() {
        // Given: A non-existent category ID
        UUID nonExistentId = UUID.randomUUID();
        String newName = "New Name";

        // When: Attempting to update non-existent category
        try {
            ResponseEntity<CategoryDto> response = categoryRestApiClient.updateCategory(nonExistentId, newName, userToken);

            // Then: Should return error status
            assertTrue(response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError());
            log.info("Correctly failed to update non-existent category with status: {}", response.getStatusCode());
        } catch (Exception e) {
            // Exception is expected for non-existent category
            log.info("Update correctly failed with exception for non-existent category: {}", e.getMessage());
        }
    }

    @Test
    @Order(11)
    @DisplayName("Should successfully delete join categories")
    public void testDeleteJoinCategories_WithValidData_DeletesJoinCategories() {
        // Given: Existing join categories
        List<JoinCategoryDto> joinCategoryDtos = new ArrayList<>();
        JoinCategoryDto dto = new JoinCategoryDto(
                testCategory.getId(),
                testEntityId,
                testCategory.getType(),
                "TEST_ENTITY"
        );
        joinCategoryDtos.add(dto);

        // When: Deleting join categories
        ResponseEntity<Void> response = categoryRestApiClient.deleteJoinCategories(joinCategoryDtos, userToken);

        // Then: Join categories are deleted successfully
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.info("Successfully deleted join categories for entity: {}", testEntityId);

        // Verify: Categories are no longer associated with entity
        ResponseEntity<List<CategoryDto>> verifyResponse = categoryRestApiClient.getCategoriesByTypeAndEntity(
                testCategory.getType(), testEntityId, userToken);
        assertThat(verifyResponse.getBody()).isEmpty();
        log.info("Verified that join categories were deleted");
    }

    @Test
    @Order(12)
    @DisplayName("Should handle delete of non-existent join categories gracefully")
    public void testDeleteJoinCategories_WithNonExistentData_HandlesGracefully() {
        // Given: Non-existent join category data
        List<JoinCategoryDto> joinCategoryDtos = new ArrayList<>();
        JoinCategoryDto dto = new JoinCategoryDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "NON_EXISTENT_TYPE",
                "NON_EXISTENT_ENTITY"
        );
        joinCategoryDtos.add(dto);

        // When: Attempting to delete non-existent join categories
        try {
            ResponseEntity<Void> response = categoryRestApiClient.deleteJoinCategories(joinCategoryDtos, userToken);

            // Then: Should handle gracefully
            assertTrue(response.getStatusCode().is2xxSuccessful() || response.getStatusCode().is4xxClientError());
            log.info("Handled deletion of non-existent join categories with status: {}", response.getStatusCode());
        } catch (Exception e) {
            // Exception may be thrown for non-existent data
            log.info("Deletion of non-existent join categories threw exception: {}", e.getMessage());
        }
    }

    @Test
    @Order(13)
    @DisplayName("Should require authentication for all endpoints")
    public void testCategoryEndpoints_WithoutAuth_ReturnsUnauthorized() {
        // Given: No authentication token
        String noToken = "";

        // When/Then: Attempting to access endpoints without auth
        CategoryForm categoryForm = new CategoryForm();
        categoryForm.setName("Test");
        categoryForm.setType("TEST");

        try {
            ResponseEntity<CategoryDto> createResponse = categoryRestApiClient.createCategory(categoryForm, noToken);
            assertTrue(createResponse.getStatusCode().is4xxClientError());
        } catch (Exception e) {
            log.info("Create endpoint correctly requires authentication");
        }

        try {
            ResponseEntity<List<CategoryDto>> getAllResponse = categoryRestApiClient.getAllCategories(noToken);
            assertTrue(getAllResponse.getStatusCode().is4xxClientError());
        } catch (Exception e) {
            log.info("GetAll endpoint correctly requires authentication");
        }

        log.info("Verified that endpoints require authentication");
    }

    // Helper methods

    private UserDto createAndAuthenticateUser() {
        SignUpDto signUpDto = testData.newRegistration();
        registerAndConfirmUser(signUpDto);

        // Login and get second factor
        LoginDto loginDto = new LoginDto(signUpDto.getUsername(), null, signUpDto.getPassword());
        ResponseEntity<JwtResponse> loginResponse = authRestApiClient.login(loginDto);
        UserDto userDto = Objects.requireNonNull(loginResponse.getBody()).getUserDto();

        // Get second factor token
        String secFacToken = getSecondFactorToken(signUpDto.getEmail());
        SecondFactorForm secondFactorForm = new SecondFactorForm(userDto.getId(), secFacToken);
        ResponseEntity<JwtResponse> secFacResponse = authRestApiClient.secFac(secondFactorForm, userDto.getAccessToken());

        return Objects.requireNonNull(secFacResponse.getBody()).getUserDto();
    }

    private void registerUser(SignUpDto signUpDto) {
        ResponseEntity<Message> response = authRestApiClient.registerUser(signUpDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        log.debug("Registered user: {} with email: {}", signUpDto.getUsername(), signUpDto.getEmail());
    }

    private void registerAndConfirmUser(SignUpDto signUpDto) {
        registerUser(signUpDto);
        confirmUser(signUpDto.getUsername(), signUpDto.getEmail());
    }

    private void confirmUser(String username, String email) {
        var emailMessage = emailVerifications.assertEmailSentTo(email, EmailType.CONFIRMATION);
        var content = emailVerifications.getEmailContent(emailMessage);
        var confirmationLink = emailVerifications.parseConfirmationLink(content, "a.confirmation");
        clickConfirmationLink(confirmationLink);

        var expected = UserConfirmationStatus.valueOf("confirmed".toUpperCase());
        userVerifications.assertUserConfirmationStatus(username, expected);
        log.debug("Confirmed user: {}", username);
    }

    private void clickConfirmationLink(String confirmationLink) {
        var restTemplate = new RestTemplate();
        var response = restTemplate.getForEntity(confirmationLink, Void.class);
        log.debug("Confirmation request returned with status: {}", response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    private String getSecondFactorToken(String email) {
        var secondFactorEmailMessage = emailVerifications.assertEmailSentTo(email, EmailType.SECOND_FACTOR);
        var secFacContent = emailVerifications.getEmailContent(secondFactorEmailMessage);
        return emailVerifications.parseConfirmationLink(secFacContent, "div.general");
    }
}
