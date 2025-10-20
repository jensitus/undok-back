package at.undok.undok.client.service;

import at.undok.undok.client.exception.CategoryNotFoundException;
import at.undok.undok.client.exception.UniqueCategoryException;
import at.undok.undok.client.mapper.inter.JoinCategoryMapper;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.dto.JoinCategoryDto;
import at.undok.undok.client.model.entity.Category;
import at.undok.undok.client.model.entity.JoinCategory;
import at.undok.undok.client.model.form.CategoryForm;
import at.undok.undok.client.model.form.JoinCategoryForm;
import at.undok.undok.client.repository.CategoryRepo;
import at.undok.undok.client.repository.JoinCategoryRepo;
import at.undok.undok.client.util.CategoryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;
    private final JoinCategoryRepo joinCategoryRepo;
    private final JoinCategoryMapper joinCategoryMapper;

    public boolean checkIfCategoryAlreadyExists(String name) {
        return categoryRepo.existsByName(name);
    }

    public CategoryDto createCategory(CategoryForm categoryForm) {
        Category category = new Category();
        category.setName(categoryForm.getName());
        category.setType(categoryForm.getType());
        category.setCreatedAt(LocalDateTime.now());
        try {
            Category c = categoryRepo.save(category);
            return modelMapper.map(c, CategoryDto.class);
        } catch (DataIntegrityViolationException e) {
            throw new UniqueCategoryException("Category already exists for this topic");
        }
    }

    public List<CategoryDto> getCategoryListByType(String type) {
        List<Category> categories = categoryRepo.getCategoriesByType(type, false);
        return mapListModelMapper(categories, CategoryDto.class);
    }

    private <S, T> List<T> mapListModelMapper(List<S> source, Class<T> targetClass) {
        return source.stream()
                     .map(element -> modelMapper.map(element, targetClass))
                     .collect(Collectors.toList());
    }

    private List<JoinCategoryDto> mapJoinCategoryList(List<JoinCategoryForm> source) {
        return source.stream()
                     .map(joinCategoryMapper::toDto)
                     .collect(Collectors.toList());
    }


    public void addJoinCategory(List<JoinCategoryForm> joinCategoryFormList) {
        for (JoinCategoryForm joinCategoryForm : joinCategoryFormList) {
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
        }
    }

    public void sortOutDeselected(List<JoinCategoryForm> joinCategoryFormList, String categoryType, UUID entityId) {

        List<JoinCategory> joinCategoryList = joinCategoryRepo.findByEntityIdAndCategoryType(entityId, categoryType);

        List<JoinCategoryForm> existingForms = joinCategoryList.stream()
                                                               .map(this::mapToJoinCategoryForm)
                                                               .toList();

        List<JoinCategoryForm> categoriesToBeDeleted = existingForms.stream()
                                                                    .filter(item -> !joinCategoryFormList.contains(item))
                                                                    .toList();

        List<JoinCategoryForm> categoriesToBeAdded = joinCategoryFormList.stream()
                                                                         .filter(item -> !existingForms.contains(item))
                                                                         .toList();

        log.info("Categories to be deleted: {}", categoriesToBeDeleted.size());
        addJoinCategory(categoriesToBeAdded);

        List<JoinCategoryDto> joinCategoryDtos = mapJoinCategoryList(categoriesToBeDeleted);
        deleteJoinCategories(joinCategoryDtos);
    }

    private JoinCategoryForm mapToJoinCategoryForm(JoinCategory joinCategory) {
        JoinCategoryForm form = new JoinCategoryForm();
        form.setCategoryId(joinCategory.getCategoryId());
        form.setEntityId(joinCategory.getEntityId());
        form.setCategoryType(joinCategory.getCategoryType());
        form.setEntityType(joinCategory.getEntityType());
        return form;
    }

    public List<CategoryDto> getCategoryListByTypeAndEntity(String categoryType, UUID entityId) {
        List<Category> categoryList = categoryRepo.getCategoryByTypeAndEntity(categoryType, entityId);
        List<CategoryDto> categoryDtoList = mapListModelMapper(categoryList, CategoryDto.class);
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

    public List<CategoryDto> getAllCategories() {
        List<Category> categoryByToBeDeleted = categoryRepo.findCategoryByToBeDeleted(false);
        return mapListModelMapper(categoryByToBeDeleted, CategoryDto.class);
    }

    public CategoryDto updateCategory(UUID id, String name) {
        if (categoryRepo.existsById(id)) {
            Optional<Category> categoryOptional = categoryRepo.findById(id);
            Category categoryToUpdate = categoryOptional.orElseThrow();
            categoryToUpdate.setName(name);
            Category savedCategory = categoryRepo.save(categoryToUpdate);
            return modelMapper.map(savedCategory, CategoryDto.class);
        } else {
            throw new CategoryNotFoundException("Category doesn't exist");
        }

    }

}
