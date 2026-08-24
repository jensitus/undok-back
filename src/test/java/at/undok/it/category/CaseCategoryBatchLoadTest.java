package at.undok.it.category;

import at.undok.it.IntegrationTestBase;
import at.undok.undok.client.model.dto.CategoryDto;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.model.entity.Category;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.JoinCategory;
import at.undok.undok.client.repository.CaseRepo;
import at.undok.undok.client.repository.CategoryRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.JoinCategoryRepo;
import at.undok.undok.client.service.CategoryService;
import at.undok.undok.client.util.CategoryType;
import at.undok.undok.client.util.StatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The client list and CSV endpoints never populate openCase, so CASE-scoped categories are
 * batch-loaded by client id instead. Covers the native projection query behind that.
 */
@DisplayName("Batch loading CASE-scoped categories for a client list")
public class CaseCategoryBatchLoadTest extends IntegrationTestBase {

    private static final String ENTITY_TYPE_CASE = "CASE";

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private JoinCategoryRepo joinCategoryRepo;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private CaseRepo caseRepo;

    private Client givenClient() {
        Client client = new Client();
        client.setKeyword("client-" + UUID.randomUUID());
        client.setStatus(StatusService.STATUS_ACTIVE);
        client.setCreatedAt(LocalDateTime.now());
        return clientRepo.saveAndFlush(client);
    }

    private Case givenCase(Client client, String status) {
        Case theCase = new Case();
        theCase.setClientId(client.getId());
        theCase.setStatus(status);
        theCase.setCreatedAt(LocalDateTime.now());
        return caseRepo.saveAndFlush(theCase);
    }

    private Category givenCategory(String name) {
        Category category = new Category();
        category.setName(name);
        category.setType(CategoryType.AUFENTHALTSTITEL);
        category.setCreatedAt(LocalDateTime.now());
        return categoryRepo.saveAndFlush(category);
    }

    private void link(Category category, Case theCase) {
        JoinCategory joinCategory = new JoinCategory();
        joinCategory.setCategoryId(category.getId());
        joinCategory.setEntityId(theCase.getId());
        joinCategory.setCategoryType(CategoryType.AUFENTHALTSTITEL);
        joinCategory.setEntityType(ENTITY_TYPE_CASE);
        joinCategory.setCreatedAt(LocalDateTime.now());
        joinCategoryRepo.saveAndFlush(joinCategory);
    }

    @Test
    @Transactional
    @DisplayName("resolves the open-case Aufenthaltstitel for several clients in one query")
    void shouldGroupCategoriesByClient() {
        Client first = givenClient();
        Client second = givenClient();
        Client withoutTitle = givenClient();

        Category blaueKarte = givenCategory("Blaue Karte EU " + UUID.randomUUID());
        Category rotWeissRot = givenCategory("Rot-Weiss-Rot Karte " + UUID.randomUUID());

        link(blaueKarte, givenCase(first, StatusService.STATUS_OPEN));
        link(rotWeissRot, givenCase(second, StatusService.STATUS_OPEN));
        givenCase(withoutTitle, StatusService.STATUS_OPEN);

        Map<UUID, List<CategoryDto>> result = categoryService.getCaseCategoriesByTypeForClients(
                CategoryType.AUFENTHALTSTITEL,
                StatusService.STATUS_OPEN,
                List.of(first.getId(), second.getId(), withoutTitle.getId()));

        // Every projection getter must resolve — a mismatched column alias silently yields null.
        assertThat(result.get(first.getId()))
                .singleElement()
                .satisfies(dto -> {
                    assertThat(dto.getId()).isEqualTo(blaueKarte.getId());
                    assertThat(dto.getName()).isEqualTo(blaueKarte.getName());
                    assertThat(dto.getType()).isEqualTo(CategoryType.AUFENTHALTSTITEL);
                });
        assertThat(result.get(second.getId()))
                .singleElement()
                .extracting(CategoryDto::getId)
                .isEqualTo(rotWeissRot.getId());
        assertThat(result).doesNotContainKey(withoutTitle.getId());
    }

    @Test
    @Transactional
    @DisplayName("ignores titles hanging off a closed case")
    void shouldIgnoreClosedCases() {
        Client client = givenClient();
        Category category = givenCategory("Niederlassungsbewilligung " + UUID.randomUUID());
        link(category, givenCase(client, StatusService.STATUS_CLOSED));

        Map<UUID, List<CategoryDto>> result = categoryService.getCaseCategoriesByTypeForClients(
                CategoryType.AUFENTHALTSTITEL,
                StatusService.STATUS_OPEN,
                List.of(client.getId()));

        assertThat(result).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("an empty client list short-circuits instead of building an invalid IN ()")
    void shouldShortCircuitOnEmptyInput() {
        assertThat(categoryService.getCaseCategoriesByTypeForClients(
                CategoryType.AUFENTHALTSTITEL, StatusService.STATUS_OPEN, List.of()))
                .isEmpty();
    }
}
