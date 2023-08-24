package at.undok.undok.client.controller;

import at.undok.common.message.Message;
import at.undok.undok.client.api.CategoryApi;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.dto.JoinCategoryDto;
import at.undok.undok.client.model.form.CategoryForm;
import at.undok.undok.client.model.form.JoinCategoryForm;
import at.undok.undok.client.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity createCategory(CategoryForm categoryForm) {
        if (categoryService.checkIfCategoryAlreadyExists(categoryForm.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category already exists");
        }
        return ResponseEntity.ok(categoryService.createCategory(categoryForm));
    }

    @Override
    public List<CategoryDto> getCategoriesByType(String type) {
        return categoryService.getCategoryListByType(type);
    }

    @Override
    public ResponseEntity addJoinCategory(List<JoinCategoryForm> categoryFormList) {
        categoryService.addJoinCategory(categoryFormList);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Message("Categories successfully added"));
    }

    @Override
    public ResponseEntity getCategoriesByTypeAndEntity(String type, UUID entityId) {
        List<CategoryDto> categoryDtos = categoryService.getCategoryListByTypeAndEntity(type, entityId);
        return ResponseEntity.ok(categoryDtos);
    }

    @Override
    public ResponseEntity deleteJoinCategories(List<JoinCategoryDto> categoryDtos) {
        categoryService.deleteJoinCategories(categoryDtos);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @Override
    public ResponseEntity<CategoryDto> updateCategory(UUID id, String name) {
        return ResponseEntity.ok(categoryService.updateCategory(id, name));
    }

}
