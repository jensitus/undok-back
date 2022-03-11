package at.undok.undok.client.controller;

import at.undok.undok.client.api.CategoryApi;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.form.CategoryForm;
import at.undok.undok.client.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
