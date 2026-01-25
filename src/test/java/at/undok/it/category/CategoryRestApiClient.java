package at.undok.it.category;

import at.undok.common.message.Message;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.dto.JoinCategoryDto;
import at.undok.undok.client.model.form.CategoryForm;
import at.undok.undok.client.model.form.JoinCategoryForm;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public class CategoryRestApiClient {

    private static final String HOST = "http://localhost:";
    private static final String CATEGORIES_PATH = "/service/undok/categories";

    private final TestRestTemplate testRestTemplate;
    private final Integer serverPort;

    public CategoryRestApiClient(TestRestTemplate testRestTemplate, int serverPort) {
        this.testRestTemplate = testRestTemplate;
        this.serverPort = serverPort;
    }

    public ResponseEntity<CategoryDto> createCategory(CategoryForm categoryForm, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + CATEGORIES_PATH + "/create";
        HttpEntity<CategoryForm> entity = new HttpEntity<>(categoryForm, httpHeaders);
        return testRestTemplate.postForEntity(url, entity, CategoryDto.class);
    }

    public ResponseEntity<List<CategoryDto>> getCategoriesByType(String type, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + CATEGORIES_PATH + "/by-type/" + type;
        HttpEntity entity = new HttpEntity(httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<CategoryDto>>() {
        });
    }

    public ResponseEntity<Message> addJoinCategory(List<JoinCategoryForm> joinCategoryFormList, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + CATEGORIES_PATH + "/add-join-category";
        HttpEntity<List<JoinCategoryForm>> entity = new HttpEntity<>(joinCategoryFormList, httpHeaders);
        return testRestTemplate.postForEntity(url, entity, Message.class);
    }

    public ResponseEntity<List<CategoryDto>> getCategoriesByTypeAndEntity(String type, UUID entityId, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + CATEGORIES_PATH + "/type/" + type + "/entity/" + entityId;
        HttpEntity entity = new HttpEntity(httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<CategoryDto>>() {
        });
    }

    public ResponseEntity<Void> deleteJoinCategories(List<JoinCategoryDto> joinCategoryDtos, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + CATEGORIES_PATH + "/join-categories";
        HttpEntity<List<JoinCategoryDto>> entity = new HttpEntity<>(joinCategoryDtos, httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
    }

    public ResponseEntity<List<CategoryDto>> getAllCategories(String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + CATEGORIES_PATH + "/all";
        HttpEntity entity = new HttpEntity(httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<CategoryDto>>() {
        });
    }

    public ResponseEntity<CategoryDto> updateCategory(UUID id, String name, String accessToken) {
        HttpHeaders httpHeaders = getHeaders(accessToken);
        String url = HOST + serverPort + CATEGORIES_PATH + "/" + id + "/update";
        HttpEntity<String> entity = new HttpEntity<>(name, httpHeaders);
        return testRestTemplate.exchange(url, HttpMethod.PUT, entity, CategoryDto.class);
    }

    private HttpHeaders getHeaders(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + accessToken);
        return httpHeaders;
    }
}
