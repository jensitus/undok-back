package at.undok.undok.client.service;

import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.dto.JoinCategoryDto;
import at.undok.undok.client.model.entity.Category;
import at.undok.undok.client.model.entity.JoinCategory;
import at.undok.undok.client.model.form.CategoryForm;
import at.undok.undok.client.model.form.JoinCategoryForm;
import at.undok.undok.client.repository.CategoryRepo;
import at.undok.undok.client.repository.JoinCategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;
    private final JoinCategoryRepo joinCategoryRepo;

    public boolean checkIfCategoryAlreadyExists(String name) {
        return categoryRepo.existsByName(name);
    }

    public CategoryDto createCategory(CategoryForm categoryForm) {
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

    public void addJoinCategory(List<JoinCategoryForm> joinCategoryFormList) {
        joinCategoryFormList.forEach(joinCategoryForm -> {
            JoinCategory joinCategory = new JoinCategory();
            joinCategory.setCategoryId(joinCategoryForm.getCategoryId());
            joinCategory.setEntityId(joinCategoryForm.getEntityId());
            joinCategory.setCategoryType(joinCategoryForm.getCategoryType());
            joinCategory.setEntityType(joinCategoryForm.getEntityType());
            joinCategory.setCreatedAt(LocalDateTime.now());
            try {
                joinCategoryRepo.save(joinCategory);
            } catch (DataIntegrityViolationException e) {
                log.info(e.getClass().getName());
                log.info(e.getClass().getCanonicalName());
                log.info(e.getClass().getTypeName());
                log.debug(e.getMessage());
                log.warn("joinCategory already exists");
            }
        });
    }

    public List<CategoryDto> getCategoryListByTypeAndEntity(String categoryType, UUID entityId) {
        List<Category> categoryList = categoryRepo.getCategoryByTypeAndEntity(categoryType, entityId);
        List<CategoryDto> categoryDtoList = mapList(categoryList, CategoryDto.class);
        return categoryDtoList;
    }

    public void deleteJoinCategories(List<JoinCategoryDto> joinCategoryDtos) {
        List<JoinCategory> joinCategories = new ArrayList<>();
        for (JoinCategoryDto joinCategoryDto : joinCategoryDtos) {
            JoinCategory joinCategory = joinCategoryRepo.findByEntityTypeAndEntityIdAndCategoryTypeAndCategoryId(joinCategoryDto.getEntityType(), joinCategoryDto.getEntityId(), joinCategoryDto.getCategoryType(), joinCategoryDto.getCategoryId());
            joinCategories.add(joinCategory);
        }
        joinCategoryRepo.deleteAll(joinCategories);
    }

}
