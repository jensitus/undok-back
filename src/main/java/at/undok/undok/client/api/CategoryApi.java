package at.undok.undok.client.api;

import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.form.CategoryForm;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/service/undok/categories")
@PreAuthorize("hasRole('USER')")
public interface CategoryApi {

    @PostMapping("/create")
    ResponseEntity createCategory(@RequestBody CategoryForm categoryForm);

    @GetMapping("/by-type/{type}")
    List<CategoryDto> getCategoriesByType(@PathVariable("type") String type);

}
