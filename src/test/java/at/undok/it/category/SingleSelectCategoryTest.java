package at.undok.it.category;

import at.undok.it.IntegrationTestBase;
import at.undok.undok.client.model.entity.Category;
import at.undok.undok.client.model.form.JoinCategoryForm;
import at.undok.undok.client.repository.CategoryRepo;
import at.undok.undok.client.repository.JoinCategoryRepo;
import at.undok.undok.client.service.CategoryService;
import at.undok.undok.client.util.CategoryType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Aufenthaltstitel is the first single-select category persisted through join_category.
 * A partial unique index enforces at most one row per (entity_id, category_type), which
 * makes the add/delete ordering inside CategoryService.sortOutDeselected load-bearing.
 */
@DisplayName("Single-select category types stored in join_category")
public class SingleSelectCategoryTest extends IntegrationTestBase {

    private static final String ENTITY_TYPE_CASE = "CASE";

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private JoinCategoryRepo joinCategoryRepo;

    private Category givenCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setType(CategoryType.AUFENTHALTSTITEL);
        category.setCreatedAt(LocalDateTime.now());
        return categoryRepo.saveAndFlush(category);
    }

    private JoinCategoryForm formFor(Category category, UUID caseId) {
        JoinCategoryForm form = new JoinCategoryForm();
        form.setCategoryId(category.getId());
        form.setEntityId(caseId);
        form.setCategoryType(CategoryType.AUFENTHALTSTITEL);
        form.setEntityType(ENTITY_TYPE_CASE);
        return form;
    }

    @Test
    @Transactional
    @DisplayName("replacing the selected value keeps exactly one row and does not break the session")
    void replacingSingleSelectValue_shouldSwapTheRow() {
        UUID caseId = UUID.randomUUID();
        Category blaueKarte = givenCategory("Blaue Karte EU " + UUID.randomUUID());
        Category rotWeissRot = givenCategory("Rot-Weiss-Rot Karte " + UUID.randomUUID());

        categoryService.sortOutDeselected(List.of(formFor(blaueKarte, caseId)),
                CategoryType.AUFENTHALTSTITEL, caseId);
        joinCategoryRepo.flush();

        assertThat(joinCategoryRepo.findByEntityIdAndCategoryType(caseId, CategoryType.AUFENTHALTSTITEL))
                .hasSize(1);

        // The regression: inserting the replacement while the old row is still present
        // violated the partial unique index, and the swallowed exception left a JoinCategory
        // with a null id in the persistence context.
        categoryService.sortOutDeselected(List.of(formFor(rotWeissRot, caseId)),
                CategoryType.AUFENTHALTSTITEL, caseId);
        joinCategoryRepo.flush();

        assertThat(joinCategoryRepo.findByEntityIdAndCategoryType(caseId, CategoryType.AUFENTHALTSTITEL))
                .singleElement()
                .extracting(jc -> jc.getCategoryId())
                .isEqualTo(rotWeissRot.getId());
    }

    @Test
    @Transactional
    @DisplayName("re-submitting the same value is a no-op")
    void resubmittingSameValue_shouldNotDuplicate() {
        UUID caseId = UUID.randomUUID();
        Category category = givenCategory("Aufenthaltsbewilligung " + UUID.randomUUID());

        categoryService.sortOutDeselected(List.of(formFor(category, caseId)),
                CategoryType.AUFENTHALTSTITEL, caseId);
        categoryService.sortOutDeselected(List.of(formFor(category, caseId)),
                CategoryType.AUFENTHALTSTITEL, caseId);
        joinCategoryRepo.flush();

        assertThat(joinCategoryRepo.findByEntityIdAndCategoryType(caseId, CategoryType.AUFENTHALTSTITEL))
                .hasSize(1);
    }

    @Test
    @Transactional
    @DisplayName("an empty selection clears the stored value")
    void emptySelection_shouldRemoveTheRow() {
        UUID caseId = UUID.randomUUID();
        Category category = givenCategory("Niederlassungsbewilligung " + UUID.randomUUID());

        categoryService.sortOutDeselected(List.of(formFor(category, caseId)),
                CategoryType.AUFENTHALTSTITEL, caseId);
        joinCategoryRepo.flush();

        categoryService.sortOutDeselected(List.of(), CategoryType.AUFENTHALTSTITEL, caseId);
        joinCategoryRepo.flush();

        assertThat(joinCategoryRepo.findByEntityIdAndCategoryType(caseId, CategoryType.AUFENTHALTSTITEL))
                .isEmpty();
    }
}
