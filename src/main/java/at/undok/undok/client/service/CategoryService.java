package at.undok.undok.client.service;

import at.undok.undok.client.exception.CategoryException;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.entity.Category;
import at.undok.undok.client.model.form.CategoryForm;
import at.undok.undok.client.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;
    private static final String CATEGORY_ALREADY_EXISTS = "diese Kategorie existiert leider schon";

    public boolean checkIfCategoryAlreadyExists(String name) {
        return categoryRepo.existsByName(name);
    }

    public CategoryDto createCategory(CategoryForm categoryForm) {
        if (checkIfCategoryAlreadyExists(categoryForm.getName())) {
            throw new CategoryException(HttpStatus.CONFLICT, CATEGORY_ALREADY_EXISTS);
        }
        Category category = new Category();
        category.setName(categoryForm.getName());
        category.setType(categoryForm.getType());
        category.setCreatedAt(LocalDateTime.now());
        Category c = categoryRepo.save(category);
        return modelMapper.map(c, CategoryDto.class);
    }

    public List<CategoryDto> getCategoryListByType(String type) {
        List<Category> categories = categoryRepo.getCategoriesByType(type);
        return mapList(categories, CategoryDto.class);
    }

    private <S, T> List<T> mapList(List<S> source, Class<T> targetClass) {
        return source.stream()
                     .map(element -> modelMapper.map(element, targetClass))
                     .collect(Collectors.toList());
    }

}
