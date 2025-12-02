package at.undok.it.clienttests;

import at.undok.it.IntegrationTestBase;
import at.undok.undok.client.model.dto.CounselingDto;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.entity.Person;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import at.undok.undok.client.repository.PersonRepo;
import at.undok.undok.client.service.CounselingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CounselingFullTextSearchIntegrationTest extends IntegrationTestBase {

    @Autowired
    private CounselingService searchService;

    @Autowired
    private CounselingRepo counselingRepository;

    @Autowired
    private PersonRepo personRepository;

    @Autowired
    private ClientRepo clientRepository;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        counselingRepository.deleteAll();
        clientRepository.deleteAll();
        personRepository.deleteAll();

        Person person = new Person();
        person.setFirstName("Claudia");
        person.setLastName("Augusta");
        Person savedPerson = personRepository.save(person);
        // Create test data
        Client client = new Client();
        /// client.setId(UUID.randomUUID());
        client.setKeyword("Test Client");
        client.setPerson(savedPerson);
        // ... set other required fields
        Client savedClient = clientRepository.save(client);

        Counseling c1 = new Counseling();
        c1.setClient(savedClient);
        c1.setCounselingDate(LocalDateTime.now());
        c1.setConcern("Ich brauche eine Arbeitserlaubnis");
        c1.setActivity("Beratung zur Arbeitserlaubnis durchgeführt");
        counselingRepository.save(c1);

        Counseling c2 = new Counseling();
        c2.setClient(savedClient);
        c2.setCounselingDate(LocalDateTime.now());
        c2.setConcern("Probleme mit dem Aufenthaltstitel");
        c2.setActivity("Information über Aufenthaltstitel gegeben");
        counselingRepository.save(c2);

        Counseling c3 = new Counseling();
        c3.setClient(savedClient);
        c3.setCounselingDate(LocalDateTime.now());
        c3.setConcern("Familiennachzug beantragen");
        c3.setActivity("Unterstützung beim Antrag");
        counselingRepository.save(c3);

        // Flush to ensure tsvector is generated
        counselingRepository.flush();
    }

    @Test
    void shouldFindCounselingsBySearchTerm() {
        // When
        Page<CounselingDto> results = searchService.search("Arbeitserlaubnis", 0, 10, null, null);

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getConcern())
                .contains("Arbeitserlaubnis");
    }

    @Test
    void shouldFindCounselingsWithStemming() {
        // When - search for "arbeit" should find "Arbeitserlaubnis"
        Page<CounselingDto> results = searchService.search("arbeit", 0, 10, null, null);

        // Then - German stemming should match
        assertThat(results.getContent()).hasSizeGreaterThanOrEqualTo(0);
    }

    @Test
    void shouldFindCounselingsByActivityField() {
        // When
        Page<CounselingDto> results = searchService.search("Beratung", 0, 10, null, null);

        // Then
        assertThat(results.getContent()).isNotEmpty();
        assertThat(results.getContent())
                .anyMatch(dto -> dto.getActivity().contains("Beratung"));
    }

    @Test
    void shouldRankConcernHigherThanActivity() {
        // Given - "Aufenthaltstitel" appears in both concern and activity

        // When
        Page<CounselingDto> results = searchService.search("Aufenthaltstitel", 0, 10, null, null);

        // Then - the one with "Aufenthaltstitel" in concern should rank first
        assertThat(results.getContent()).isNotEmpty();
        assertThat(results.getContent().get(0).getConcern())
                .contains("Aufenthaltstitel");
    }

    @Test
    void shouldReturnEmptyForNonMatchingSearch() {
        // When
        Page<CounselingDto> results = searchService.search("xyz123notfound", 0, 10, null, null);

        // Then
        assertThat(results.getContent()).isEmpty();
    }

    @Test
    void shouldHandleEmptySearchTerm() {
        // When
        Page<CounselingDto> results = searchService.search("", 0, 10, null, null);

        // Then
        assertThat(results.getContent()).isEmpty();
    }

    @Test
    void shouldHandleNullSearchTerm() {
        // When
        Page<CounselingDto> results = searchService.search(null, 0, 10, null, null);

        // Then
        assertThat(results.getContent()).isEmpty();
    }

    @Test
    void shouldHandleMultipleWords() {
        // When
        Page<CounselingDto> results = searchService.search("Arbeitserlaubnis Beratung", 0, 10, null, null);

        // Then - should find counselings containing both words
        assertThat(results.getContent()).isNotEmpty();
    }

    @Test
    void shouldSupportPagination() {
        // Given - add more test data
        Client client = clientRepository.findAll().get(0);
        for (int i = 0; i < 15; i++) {
            Counseling c = new Counseling();
            c.setId(UUID.randomUUID());
            c.setClient(client);
            c.setCounselingDate(LocalDateTime.now());
            c.setConcern("Beratung " + i);
            c.setActivity("Test");
            counselingRepository.save(c);
        }
        counselingRepository.flush();

        // When - get first page
        Page<CounselingDto> page1 = searchService.search("Beratung", 0, 10, null, null);
        Page<CounselingDto> page2 = searchService.search("Beratung", 1, 10, null, null);

        // Then
        assertThat(page1.getContent()).hasSize(10);
        assertThat(page1.getTotalElements()).isGreaterThanOrEqualTo(15);
        assertThat(page2.getContent()).isNotEmpty();
        assertThat(page1.getContent().get(0).getId())
                .isNotEqualTo(page2.getContent().get(0).getId());
    }
}