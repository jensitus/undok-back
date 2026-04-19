package at.undok.it.search;

import at.undok.it.IntegrationTestBase;
import at.undok.undok.client.model.dto.UnifiedSearchResponse;
import at.undok.undok.client.model.entity.Case;
import at.undok.undok.client.model.entity.Category;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.entity.JoinCategory;
import at.undok.undok.client.repository.CaseRepo;
import at.undok.undok.client.repository.CategoryRepo;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import at.undok.undok.client.repository.JoinCategoryRepo;
import at.undok.undok.client.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class SearchServiceTest extends IntegrationTestBase {

    @Autowired private SearchService searchService;
    @Autowired private ClientRepo clientRepo;
    @Autowired private CounselingRepo counselingRepo;
    @Autowired private CaseRepo caseRepo;
    @Autowired private CategoryRepo categoryRepo;
    @Autowired private JoinCategoryRepo joinCategoryRepo;

    @BeforeEach
    void cleanUp() {
        joinCategoryRepo.deleteAll();
        counselingRepo.deleteAll();
        caseRepo.deleteAll();
        clientRepo.deleteAll();
        categoryRepo.deleteAll();
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Client savedClient(String keyword) {
        Client c = new Client();
        c.setKeyword(keyword);
        c.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        return clientRepo.save(c);
    }

    private Counseling savedCounseling(Client client, LocalDateTime date) {
        Counseling co = new Counseling();
        co.setClient(client);
        co.setCounselingDate(date);
        return counselingRepo.save(co);
    }

    private Case savedCase(Client client) {
        Case ca = new Case();
        ca.setName("Test Case " + client.getKeyword());
        ca.setStatus("OPEN");
        ca.setStartDate(LocalDate.of(2024, 1, 1));
        ca.setCreatedAt(LocalDateTime.of(2024, 1, 1, 0, 0));
        ca.setClientId(client.getId());
        return caseRepo.save(ca);
    }

    private Category savedCategory(String name, String type) {
        Category cat = new Category();
        cat.setName(name);
        cat.setType(type);
        return categoryRepo.save(cat);
    }

    private void linkCategoryToCase(Category category, Case ca) {
        JoinCategory jc = new JoinCategory();
        jc.setCategoryId(category.getId());
        jc.setEntityId(ca.getId());
        jc.setCategoryType(category.getType());
        jc.setEntityType("CASE");
        jc.setCreatedAt(LocalDateTime.now());
        joinCategoryRepo.save(jc);
    }

    private void linkCategoryToCounseling(Category category, Counseling counseling) {
        JoinCategory jc = new JoinCategory();
        jc.setCategoryId(category.getId());
        jc.setEntityId(counseling.getId());
        jc.setCategoryType(category.getType());
        jc.setEntityType("COUNSELING");
        jc.setCreatedAt(LocalDateTime.now());
        joinCategoryRepo.save(jc);
    }

    private static final LocalDateTime WIDE_START = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final LocalDateTime WIDE_END   = LocalDateTime.of(2099, 12, 31, 23, 59);

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    void multiWordOrSearch_shouldFindBothClientsWhenEachMatchesOneWord() {
        // client1 has INDUSTRY_UNION category "Tourismus" linked via CASE
        Client client1 = savedClient("tourismus_client");
        Case case1 = savedCase(client1);
        Category tourismus = savedCategory("Tourismus (Vida)", "INDUSTRY_UNION");
        linkCategoryToCase(tourismus, case1);

        // client2 has ACTIVITY category "Vermittlung" linked via COUNSELING
        Client client2 = savedClient("vermittlung_client");
        Counseling co2 = savedCounseling(client2, LocalDateTime.of(2024, 3, 1, 0, 0));
        Category vermittlung = savedCategory("Vermittlung", "ACTIVITY");
        linkCategoryToCounseling(vermittlung, co2);

        // Search "Tourismus Vermittlung" → both clients found (OR semantics)
        UnifiedSearchResponse response = searchService.searchAll("Tourismus Vermittlung", WIDE_START, WIDE_END, 0, 10);

        assertThat(response.getClients()).hasSize(2);
        assertThat(response.getClients())
                .extracting("keyword")
                .containsExactlyInAnyOrder("tourismus_client", "vermittlung_client");
    }

    @Test
    void activityCategory_linkedToCounseling_shouldBeFoundBySearch() {
        Client client = savedClient("activity_client");
        Counseling co = savedCounseling(client, LocalDateTime.of(2024, 5, 1, 0, 0));
        Category activity = savedCategory("Lohnberatung", "ACTIVITY");
        linkCategoryToCounseling(activity, co);

        UnifiedSearchResponse response = searchService.searchAll("Lohnberatung", WIDE_START, WIDE_END, 0, 10);

        assertThat(response.getClients()).hasSize(1);
        assertThat(response.getClients().get(0).getKeyword()).isEqualTo("activity_client");
    }

    @Test
    void activityCategory_counselingOutsideDateRange_shouldNotBeFound() {
        Client client = savedClient("future_counseling_client");
        // Counseling date is after the search range
        Counseling co = savedCounseling(client, LocalDateTime.of(2026, 6, 25, 0, 0));
        Category activity = savedCategory("Vermittlung", "ACTIVITY");
        linkCategoryToCounseling(activity, co);

        LocalDateTime rangeEnd = LocalDateTime.of(2026, 4, 19, 23, 59);
        UnifiedSearchResponse response = searchService.searchAll("Vermittlung", WIDE_START, rangeEnd, 0, 10);

        assertThat(response.getClients()).isEmpty();
    }

    @Test
    void activityCategory_counselingInsideDateRange_shouldBeFound() {
        Client client = savedClient("in_range_counseling_client");
        Counseling co = savedCounseling(client, LocalDateTime.of(2024, 6, 1, 0, 0));
        Category activity = savedCategory("Lohndumping", "ACTIVITY");
        linkCategoryToCounseling(activity, co);

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2024, 12, 31, 23, 59);
        UnifiedSearchResponse response = searchService.searchAll("Lohndumping", start, end, 0, 10);

        assertThat(response.getClients()).hasSize(1);
    }

    @Test
    void industryUnionCategory_caseOutsideDateRange_shouldNotBeFound() {
        Client client = savedClient("old_case_client");
        // Case created_at will be now (recent), but we search with a very old range
        Case ca = savedCase(client);
        Category cat = savedCategory("Gastgewerbe", "INDUSTRY_UNION");
        linkCategoryToCase(cat, ca);

        LocalDateTime start = LocalDateTime.of(2000, 1, 1, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2000, 12, 31, 23, 59);
        UnifiedSearchResponse response = searchService.searchAll("Gastgewerbe", start, end, 0, 10);

        assertThat(response.getClients()).isEmpty();
    }

    @Test
    void matchedCategories_shouldContainCorrectCategoryType() {
        Client client = savedClient("typed_category_client");

        // INDUSTRY_UNION via CASE
        Case ca = savedCase(client);
        Category industryUnion = savedCategory("Baugewerbe", "INDUSTRY_UNION");
        linkCategoryToCase(industryUnion, ca);

        // ACTIVITY via COUNSELING
        Counseling co = savedCounseling(client, LocalDateTime.of(2024, 4, 1, 0, 0));
        Category activity = savedCategory("Rechtsberatung", "ACTIVITY");
        linkCategoryToCounseling(activity, co);

        UnifiedSearchResponse response = searchService.searchAll("Baugewerbe Rechtsberatung", WIDE_START, WIDE_END, 0, 10);

        assertThat(response.getClients()).hasSize(1);
        assertThat(response.getClients().get(0).getMatchedCategories())
                .extracting("categoryType")
                .containsExactlyInAnyOrder("INDUSTRY_UNION", "ACTIVITY");
        assertThat(response.getClients().get(0).getMatchedCategories())
                .extracting("categoryName")
                .containsExactlyInAnyOrder("Baugewerbe", "Rechtsberatung");
    }

    @Test
    void quotedPhrase_shouldBeSearchedAsWholePhrase() {
        // client1 has category "Soziale Arbeit" (both words together)
        Client client1 = savedClient("social_work_client");
        Case ca1 = savedCase(client1);
        Category soziale = savedCategory("Soziale Arbeit", "INDUSTRY_UNION");
        linkCategoryToCase(soziale, ca1);

        // client2 has category "Soziale Betreuung" (only first word matches)
        Client client2 = savedClient("social_care_client");
        Case ca2 = savedCase(client2);
        Category betreuung = savedCategory("Soziale Betreuung", "INDUSTRY_UNION");
        linkCategoryToCase(betreuung, ca2);

        // Unquoted: "Soziale Arbeit" → OR → both clients found
        UnifiedSearchResponse orResponse = searchService.searchAll("Soziale Arbeit", WIDE_START, WIDE_END, 0, 10);
        assertThat(orResponse.getClients()).hasSize(2);

        // Quoted phrase: only client1 matches exactly "Soziale Arbeit"
        UnifiedSearchResponse phraseResponse = searchService.searchAll("\"Soziale Arbeit\"", WIDE_START, WIDE_END, 0, 10);
        assertThat(phraseResponse.getClients()).hasSize(1);
        assertThat(phraseResponse.getClients().get(0).getKeyword()).isEqualTo("social_work_client");
    }

    @Test
    void multiWordOrSearch_noDuplicates_whenClientMatchesBothWords() {
        // Client has TWO categories matching the two search words
        Client client = savedClient("double_match_client");
        Case ca = savedCase(client);
        Category cat1 = savedCategory("Tourismus (Vida)", "INDUSTRY_UNION");
        Category cat2 = savedCategory("Vermittlung", "ACTIVITY");
        linkCategoryToCase(cat1, ca);
        Counseling co = savedCounseling(client, LocalDateTime.of(2024, 3, 1, 0, 0));
        linkCategoryToCounseling(cat2, co);

        UnifiedSearchResponse response = searchService.searchAll("Tourismus Vermittlung", WIDE_START, WIDE_END, 0, 10);

        // Client should appear only once despite matching both words
        assertThat(response.getClients()).hasSize(1);
        assertThat(response.getClients().get(0).getMatchedCategories()).hasSize(2);
    }
}
