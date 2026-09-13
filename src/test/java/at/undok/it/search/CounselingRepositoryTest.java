package at.undok.it.search;

import at.undok.it.IntegrationTestBase;
import at.undok.undok.client.model.dto.UnifiedSearchResponse;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.model.entity.Category;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.entity.JoinCategory;
import at.undok.undok.client.model.entity.Task;
import at.undok.undok.client.repository.CaseRepo;
import at.undok.undok.client.repository.CategoryRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import at.undok.undok.client.repository.JoinCategoryRepo;
import at.undok.undok.client.repository.TaskRepo;
import at.undok.undok.client.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CounselingRepositoryTest extends IntegrationTestBase {

    @Autowired
    private CounselingRepo counselingRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ClientRepo clientRepo;

    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private CaseRepo caseRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private JoinCategoryRepo joinCategoryRepo;

    @BeforeEach
    void setUp() {
        joinCategoryRepo.deleteAll();
        counselingRepository.deleteAll();
        clientRepo.deleteAll();
        caseRepo.deleteAll();
        taskRepo.deleteAll();
        categoryRepo.deleteAll();

        // Create test data

        Client client1 = new Client();
        client1.setFirstName("Emil");
        client1.setLastName("Hundelümmel");
        client1.setKeyword("emil_hundelümmel");
        client1.setCreatedAt(LocalDateTime.of(2024, 6, 1, 0, 0));
        Client savedClient = clientRepo.save(client1);

        Counseling c1 = new Counseling();
        c1.setConcern("Angst und Behandlung von Depression");
        c1.setActivity("Wöchentliche Therapie Sitzungen");
        c1.setCounselingDate(LocalDateTime.of(2024, 6, 15, 0, 0));
        c1.setClient(savedClient);
        counselingRepository.save(c1);

        Counseling c2 = new Counseling();
        c2.setConcern("stress management");
        c2.setActivity("Entspannungstechniken");
        c2.setCounselingDate(LocalDateTime.of(2024, 8, 20, 0, 0));
        c2.setClient(savedClient);
        counselingRepository.save(c2);

        Counseling c3 = new Counseling();
        c3.setConcern("Chef riecht streng. Das triggert des Hundelümmels Allergie");
        c3.setActivity("olfaktorische therapie");
        c3.setCounselingDate(LocalDateTime.of(2024, 8, 20, 0, 0));
        c3.setClient(savedClient);
        counselingRepository.save(c3);

        Case counselingCase = new Case();
        counselingCase.setName("Test Case");
        counselingCase.setStatus("OPEN");
        counselingCase.setStartDate(LocalDate.of(2024, 6, 1));
        counselingCase.setClientId(savedClient.getId());
        Case savedCase = caseRepo.save(counselingCase);

        Task task = new Task();
        task.setCaseEntity(savedCase);
        task.setStatus("OPEN");
        task.setTitle("Deo kaufen");
        task.setDescription("Deo für den Hundelümmel kaufen");
        task.setDueDate(LocalDate.now().plusDays(1));
        task.setCreatedBy("testUser");
        task.setCreatedAt(LocalDateTime.of(2024, 6, 1, 0, 0));
        taskRepo.save(task);

    }

    @Test
    void shouldFindCounselingsClientsAndCases() {
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 8, 30, 0, 0);
        UnifiedSearchResponse unifiedSearchResponse = searchService.searchAll("Hundelümmel", start, end, 0, 2);
        assertThat(unifiedSearchResponse.getPagination().getTotalCounselings()).isEqualTo(1);
        assertThat(unifiedSearchResponse.getPagination().getTotalClients()).isEqualTo(1);
        assertThat(unifiedSearchResponse.getPagination().getTotalTasks()).isEqualTo(1);
        assertThat(unifiedSearchResponse.getCounselings()).hasSize(1);
        assertThat(unifiedSearchResponse.getClients()).hasSize(1);
        assertThat(unifiedSearchResponse.getTasks()).hasSize(0);
        UnifiedSearchResponse unifiedSearchResponse2 = searchService.searchAll("Hundelümmel", start, end, 1, 2);
        assertThat(unifiedSearchResponse2.getPagination().getTotalCounselings()).isEqualTo(1);
        assertThat(unifiedSearchResponse2.getPagination().getTotalClients()).isEqualTo(1);
        assertThat(unifiedSearchResponse2.getPagination().getTotalTasks()).isEqualTo(1);
        assertThat(unifiedSearchResponse2.getCounselings()).hasSize(0);
        assertThat(unifiedSearchResponse2.getClients()).hasSize(0);
        assertThat(unifiedSearchResponse2.getTasks()).hasSize(1);
    }

    @Test
    void shouldFindCounselingsByFullTextSearch() {
        List<Counseling> results = counselingRepository.fullTextSearch("angst");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getConcern()).contains("Angst");
    }

    @Test
    void shouldSupportMultiWordSearch() {
        List<Counseling> results = counselingRepository.fullTextSearch("Therapie Sitzungen");

        assertThat(results).isNotEmpty();
    }

    @Test
    void shouldSupportStemming() {
        // Searching "relax" should find "relaxation"
        List<Counseling> results = counselingRepository.fullTextSearch("Behandlungen");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getActivity()).contains("Sitzungen");
    }

    @Test
    void shouldFilterByDateRange() {
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 30, 0, 0);

        List<Counseling> results = counselingRepository
                .fullTextSearchWithDateRange("Therapie", start, end);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getCounselingDate()).isBetween(start, end);
    }

    @Test
    void shouldHandleNullDates() {
        UnifiedSearchResponse unifiedSearchResponse = searchService.searchAll("Therapie", null, null, 0, 2);
        assertThat(unifiedSearchResponse.getCounselings()).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyListForNoMatches() {
        List<Counseling> results = counselingRepository
                .fullTextSearch("nonexistent_term_xyz");

        assertThat(results).isEmpty();
    }

    @Test
    void shouldSupportPagination() {

        UnifiedSearchResponse unifiedSearchResponse1 = searchService.searchAll("Therapie", 0, 2);
        UnifiedSearchResponse unifiedSearchResponse2 = searchService.searchAll("Therapie", 1, 1);


        assertThat(unifiedSearchResponse1.getPagination().getTotalPages()).isEqualTo(1);
        assertThat(unifiedSearchResponse1.getPagination().getPageSize()).isEqualTo(2);
        assertThat(unifiedSearchResponse2.getPagination().getPageSize()).isEqualTo(1);
        assertThat(unifiedSearchResponse2.getCounselings().get(0).getId()).isNotEqualTo(unifiedSearchResponse1.getCounselings().get(0).getId());
    }

    @Test
    void shouldSupportPaginationAndReturnUnifiedSearchResponse() {
        LocalDateTime start = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 6, 30, 0, 0);
        UnifiedSearchResponse unifiedSearchResponse = searchService.searchAll("Therapie", start, end, 0, 2);
        assertThat(unifiedSearchResponse.getCounselings()).hasSize(1);
    }

    @Test
    void shouldCountCorrectly() {
        long count = counselingRepository.countFullTextSearch("Therapie");

        assertThat(count).isEqualTo(2);
    }

    private LocalDateTime getStartDate() {
        return LocalDateTime.of(2024, 6, 1, 0, 0);
    }

    private LocalDateTime getEndDate() {
        return LocalDateTime.of(2024, 6, 30, 0, 0);
    }

    @Test
    void shouldFindClientByIndustryUnionCategoryName() {
        // Create a client that won't match fulltext search for "Tourismus"
        Client client = new Client();
        client.setFirstName("Max");
        client.setLastName("Mustermann");
        client.setKeyword("max_mustermann");
        client.setCreatedAt(LocalDateTime.of(2024, 6, 1, 0, 0));
        Client savedClient = clientRepo.save(client);

        // Create a case for the client (categories are linked to cases)
        Case clientCase = new Case();
        clientCase.setName("Tourismus Case");
        clientCase.setStatus("OPEN");
        clientCase.setStartDate(LocalDate.of(2024, 6, 1));
        clientCase.setCreatedAt(LocalDateTime.of(2024, 6, 1, 0, 0));
        clientCase.setClientId(savedClient.getId());
        Case savedCase = caseRepo.save(clientCase);

        // Create an INDUSTRY_UNION category
        Category category = new Category();
        category.setName("Tourismus (Vida)");
        category.setType("INDUSTRY_UNION");
        Category savedCategory = categoryRepo.save(category);

        // Link category to case
        JoinCategory joinCategory = new JoinCategory();
        joinCategory.setCategoryId(savedCategory.getId());
        joinCategory.setEntityId(savedCase.getId());
        joinCategory.setCategoryType("INDUSTRY_UNION");
        joinCategory.setEntityType("CASE");
        joinCategory.setCreatedAt(LocalDateTime.now());
        joinCategoryRepo.save(joinCategory);

        // Search for the category name - should find the client
        UnifiedSearchResponse response = searchService.searchAll("Tourismus", LocalDateTime.of(2000, 1, 1, 0, 0), LocalDateTime.of(2026, 2, 17, 23, 59), 0, 10);
        assertThat(response.getClients()).hasSize(1);
        assertThat(response.getClients().get(0).getKeyword()).isEqualTo("max_mustermann");
        assertThat(response.getClients().get(0).getMatchedCategories())
                .extracting("categoryName")
                .containsExactly("Tourismus (Vida)");

        // Partial match should also work
        UnifiedSearchResponse partialResponse = searchService.searchAll("Tour", 0, 10);
        assertThat(partialResponse.getClients()).hasSize(1);
        assertThat(partialResponse.getClients().get(0).getMatchedCategories())
                .extracting("categoryName")
                .containsExactly("Tourismus (Vida)");
    }

    @Test
    void shouldFindClientByCategoryNameWithUmlaut() {
        Client client = new Client();
        client.setFirstName("Jana");
        client.setLastName("Bauer");
        client.setKeyword("jana_bauer");
        client.setCreatedAt(LocalDateTime.of(2024, 6, 1, 0, 0));
        Client savedClient = clientRepo.save(client);

        Case clientCase = new Case();
        clientCase.setName("LEFÖ Case");
        clientCase.setStatus("OPEN");
        clientCase.setStartDate(LocalDate.of(2024, 6, 1));
        clientCase.setClientId(savedClient.getId());
        Case savedCase = caseRepo.save(clientCase);

        Counseling counseling = new Counseling();
        counseling.setConcern("test");
        counseling.setCounselingDate(LocalDateTime.of(2024, 6, 15, 0, 0));
        counseling.setClient(savedClient);
        Counseling savedCounseling = counselingRepository.save(counseling);

        Category category = new Category();
        category.setName("Vermittlung/Kontakt LEFÖ IBF");
        category.setType("ACTIVITY");
        Category savedCategory = categoryRepo.save(category);

        JoinCategory joinCategory = new JoinCategory();
        joinCategory.setCategoryId(savedCategory.getId());
        joinCategory.setEntityId(savedCounseling.getId());
        joinCategory.setCategoryType("ACTIVITY");
        joinCategory.setEntityType("COUNSELING");
        joinCategory.setCreatedAt(LocalDateTime.now());
        joinCategoryRepo.save(joinCategory);

        UnifiedSearchResponse response = searchService.searchAll("lefö", 0, 10);
        assertThat(response.getClients()).hasSize(1);
        assertThat(response.getClients().get(0).getKeyword()).isEqualTo("jana_bauer");
    }

    @Test
    void shouldNotDuplicateClientInSearchResults() {
        // Create a client with "Tourismus" in the comment (matches fulltext)
        Client client = new Client();
        client.setFirstName("Anna");
        client.setLastName("Schmidt");
        client.setKeyword("anna_schmidt");
        client.setComment("Arbeitet im Tourismus");
        client.setCreatedAt(LocalDateTime.of(2024, 6, 1, 0, 0));
        Client savedClient = clientRepo.save(client);

        // Create a case for the client (categories are linked to cases)
        Case clientCase = new Case();
        clientCase.setName("Duplicate Test Case");
        clientCase.setStatus("OPEN");
        clientCase.setStartDate(LocalDate.of(2024, 6, 1));
        clientCase.setClientId(savedClient.getId());
        Case savedCase = caseRepo.save(clientCase);

        // Also link case to Tourismus category
        Category category = new Category();
        category.setName("Tourismus (Vida)");
        category.setType("INDUSTRY_UNION");
        Category savedCategory = categoryRepo.save(category);

        JoinCategory joinCategory = new JoinCategory();
        joinCategory.setCategoryId(savedCategory.getId());
        joinCategory.setEntityId(savedCase.getId());
        joinCategory.setCategoryType("INDUSTRY_UNION");
        joinCategory.setEntityType("CASE");
        joinCategory.setCreatedAt(LocalDateTime.now());
        joinCategoryRepo.save(joinCategory);

        // Search should return the client only once (not duplicated)
        UnifiedSearchResponse response = searchService.searchAll("Tourismus", 0, 10);
        assertThat(response.getClients()).hasSize(1);
        assertThat(response.getPagination().getTotalClients()).isEqualTo(1);
    }

}
