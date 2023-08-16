package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.dto.JoinCategoryDto;
import at.undok.undok.client.model.form.CategoryForm;
import at.undok.undok.client.model.form.JoinCategoryForm;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/service/undok/categories")
@PreAuthorize("hasRole('USER')")
public interface CategoryApi {

    @PostMapping("/create")
    ResponseEntity createCategory(@RequestBody CategoryForm categoryForm);

    @GetMapping("/by-type/{type}")
    List<CategoryDto> getCategoriesByType(@PathVariable("type") String type);

    @PostMapping("/add-join-category")
    ResponseEntity addJoinCategory(@RequestBody List<JoinCategoryForm> categoryFormList);

    @GetMapping("/type/{type}/entity/{entity_id}")
    ResponseEntity getCategoriesByTypeAndEntity(@PathVariable("type") String type, @PathVariable("entity_id") UUID entityId);

    @DeleteMapping("/join-categories")
    ResponseEntity deleteJoinCategories(@RequestBody List<JoinCategoryDto> categoryDtos);

    @GetMapping("/all")
    ResponseEntity<List<CategoryDto>> getAllCategories();

}
