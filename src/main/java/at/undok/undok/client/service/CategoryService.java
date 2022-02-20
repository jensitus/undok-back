package at.undok.undok.client.service;

import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.entity.Category;
import at.undok.undok.client.model.form.CategoryForm;
import at.undok.undok.client.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;

    public CategoryDto createCategory(CategoryForm categoryForm) {
        Category category = new Category();
        category.setName(categoryForm.getName());
        category.setType(categoryForm.getType());
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
