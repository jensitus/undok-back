package at.undok.undok.client.service;

import at.undok.undok.client.mapper.inter.ClientMapper;
import at.undok.undok.client.mapper.inter.CounselingMapper;
import at.undok.undok.client.model.dto.*;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchService {

    private final CounselingRepo counselingRepository;
    private final ClientRepo clientRepository;
    private final CounselingMapper counselingMapper;
    private final ClientMapper clientMapper;

    public SearchService(CounselingRepo counselingRepository,
                         ClientRepo clientRepository, CounselingMapper counselingMapper, ClientMapper clientMapper) {
        this.counselingRepository = counselingRepository;
        this.clientRepository = clientRepository;
        this.counselingMapper = counselingMapper;
        this.clientMapper = clientMapper;
    }

    /**
     * Unified paginated search across both counselings and clients tables with date range
     *
     * @param searchTerm the search query
     * @param startDate filter start date (optional)
     * @param endDate filter end date (optional)
     * @param page the page number (0-based)
     * @param size the page size
     * @return unified response containing paginated results from both tables
     */
    public UnifiedSearchResponse searchAll(String searchTerm, LocalDateTime startDate,
                                           LocalDateTime endDate, int page, int size) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            PaginationInfo emptyPagination = new PaginationInfo(page, size, 0, 0);
            return new UnifiedSearchResponse(List.of(), List.of(), emptyPagination);
        }

        String trimmedSearch = searchTerm.trim();

        // If no date range specified, use the simpler method without date filtering
        if (startDate == null && endDate == null) {
            return searchAll(trimmedSearch, page, size);
        }

        // Get total counts for pagination metadata
        long totalCounselings = countCounselingsWithDateRange(trimmedSearch, startDate, endDate);
        long totalClients = countClientsWithDateRange(trimmedSearch, startDate, endDate);
        long totalElements = totalCounselings + totalClients;

        // Calculate how many results from each table for this page
        int offset = page * size;

        List<CounselingSearchResult> counselingDtos = new ArrayList<>();
        List<ClientSearchResult> clientDtos = new ArrayList<>();

        if (offset < totalElements) {
            if (offset < totalCounselings) {
                // Still have counselings to show on this page
                int counselingsToFetch = (int) Math.min(size, totalCounselings - offset);
                List<Counseling> counselingResults = searchCounselingsWithDateRange(
                        trimmedSearch, startDate, endDate, counselingsToFetch, offset);
                counselingDtos = counselingResults.stream()
                                                  .map(CounselingSearchResult::new)
                                                  .collect(Collectors.toList());

                // Fill the remaining space with clients if needed
                int remaining = size - counselingDtos.size();
                if (remaining > 0 && totalClients > 0) {
                    List<Client> clientResults = searchClientsWithDateRange(
                            trimmedSearch, startDate, endDate, remaining, 0);
                    clientDtos = clientResults.stream()
                                              .map(ClientSearchResult::new)
                                              .collect(Collectors.toList());
                }
            } else {
                // Past all counselings, show only clients
                int clientOffset = offset - (int) totalCounselings;
                List<Client> clientResults = searchClientsWithDateRange(
                        trimmedSearch, startDate, endDate, size, clientOffset);
                clientDtos = clientResults.stream()
                                          .map(ClientSearchResult::new)
                                          .collect(Collectors.toList());
            }
        }

        PaginationInfo pagination = new PaginationInfo(page, size, totalCounselings, totalClients);
        return new UnifiedSearchResponse(counselingDtos, clientDtos, pagination);
    }

    /**
     * Unified paginated search across both counselings and clients tables
     *
     * @param searchTerm the search query
     * @param page the page number (0-based)
     * @param size the page size
     * @return unified response containing paginated results from both tables
     */
    public UnifiedSearchResponse searchAll(String searchTerm, int page, int size) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            PaginationInfo emptyPagination = new PaginationInfo(page, size, 0, 0);
            return new UnifiedSearchResponse(List.of(), List.of(), emptyPagination);
        }

        String trimmedSearch = searchTerm.trim();

        // Get total counts for pagination metadata
        long totalCounselings = counselingRepository.countFullTextSearch(trimmedSearch);
        long totalClients = clientRepository.countFullTextSearch(trimmedSearch);
        long totalElements = totalCounselings + totalClients;

        // Calculate how many results from each table for this page
        int offset = page * size;

        List<CounselingSearchResult> counselingDtos = new ArrayList<>();
        List<ClientSearchResult> clientDtos = new ArrayList<>();

        if (offset < totalElements) {
            // Fetch results proportionally from both tables
            // Or you can customize the logic based on your needs

            if (offset < totalCounselings) {
                // Still have counselings to show on this page
                int counselingsToFetch = (int) Math.min(size, totalCounselings - offset);
                List<Counseling> counselingResults = counselingRepository
                        .fullTextSearchWithPagination(trimmedSearch, counselingsToFetch, offset);
                counselingDtos = counselingResults.stream()
                                                  .map(CounselingSearchResult::new)
                                                  .collect(Collectors.toList());

                // Fill remaining space with clients if needed
                int remaining = size - counselingDtos.size();
                if (remaining > 0 && totalClients > 0) {
                    List<Client> clientResults = clientRepository
                            .fullTextSearchWithPagination(trimmedSearch, remaining, 0);
                    clientDtos = clientResults.stream()
                                              .map(ClientSearchResult::new)
                                              .collect(Collectors.toList());
                }
            } else {
                // Past all counselings, show only clients
                int clientOffset = offset - (int) totalCounselings;
                List<Client> clientResults = clientRepository
                        .fullTextSearchWithPagination(trimmedSearch, size, clientOffset);
                clientDtos = clientResults.stream()
                                          .map(ClientSearchResult::new)
                                          .collect(Collectors.toList());
            }
        }

        PaginationInfo pagination = new PaginationInfo(page, size, totalCounselings, totalClients);
        return new UnifiedSearchResponse(counselingDtos, clientDtos, pagination);
    }

    /**
     * Unified search without pagination - returns all results
     *
     * @param searchTerm the search query
     * @return unified response containing all results from both tables
     */
    public UnifiedSearchResponse searchAll(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            PaginationInfo emptyPagination = new PaginationInfo(0, 0, 0, 0);
            return new UnifiedSearchResponse(List.of(), List.of(), emptyPagination);
        }

        String trimmedSearch = searchTerm.trim();

        // Search both tables
        List<Counseling> counselingResults = counselingRepository.fullTextSearch(trimmedSearch);
        List<Client> clientResults = clientRepository.fullTextSearch(trimmedSearch);

        // Convert to DTOs
        List<CounselingSearchResult> counselingDtos = counselingResults.stream()
                                                                       .map(CounselingSearchResult::new)
                                                                       .collect(Collectors.toList());

        List<ClientSearchResult> clientDtos = clientResults.stream()
                                                           .map(ClientSearchResult::new)
                                                           .collect(Collectors.toList());

        long totalCounselings = counselingResults.size();
        long totalClients = clientResults.size();
        PaginationInfo pagination = new PaginationInfo(0,
                                                       (int)(totalCounselings + totalClients), totalCounselings, totalClients);

        return new UnifiedSearchResponse(counselingDtos, clientDtos, pagination);
    }

    /**
     * Search counselings by term
     * Supports multi-word searches, phrases, and boolean operators
     *
     * @param searchTerm the search query (e.g., "mental health", "anxiety OR depression")
     * @return list of matching counselings ordered by relevance
     */
    public List<CounselingDto> searchCounselings(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        List<Counseling> counselingList = counselingRepository.fullTextSearch(searchTerm.trim());
        return counselingList.stream().map(counselingMapper::toDto).toList();
    }

    /**
     * Search counselings with pagination
     */
    public List<CounselingDto> searchCounselings(String searchTerm, int page, int size) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        int offset = page * size;
        List<Counseling> counselingList = counselingRepository.fullTextSearchWithPagination(
                searchTerm.trim(), size, offset
        );
        return counselingList.stream().map(counselingMapper::toDto).toList();
    }

    /**
     * Search clients by term
     * Supports multi-word searches, phrases, and boolean operators
     *
     * @param searchTerm the search query (e.g., "John Smith", "keyword:urgent")
     * @return list of matching clients ordered by relevance
     */
    public List<ClientDto> searchClients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return clientRepository.fullTextSearch(searchTerm.trim()).stream().map(clientMapper::toDto).toList();
    }

    /**
     * Search clients with pagination
     */
    public List<ClientDto> searchClients(String searchTerm, int page, int size) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        int offset = page * size;
        return clientRepository.fullTextSearchWithPagination(searchTerm.trim(),
                                                             size,
                                                             offset)
                               .stream()
                               .map(clientMapper::toDto)
                               .toList();
    }

    /**
     * Count matching clients for pagination
     */
    public long countClientSearchResults(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return 0;
        }
        return clientRepository.countFullTextSearch(searchTerm.trim());
    }

    /**
     * Helper method to count counselings with date range handling
     */
    private long countCounselingsWithDateRange(String searchTerm, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return counselingRepository.countFullTextSearch(searchTerm);
        }
        return counselingRepository.countFullTextSearchWithDateRange(searchTerm, startDate, endDate);
    }

    /**
     * Helper method to count clients with date range handling
     */
    private long countClientsWithDateRange(String searchTerm, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return clientRepository.countFullTextSearch(searchTerm);
        }
        return clientRepository.countFullTextSearchWithDateRange(searchTerm, startDate, endDate);
    }

    /**
     * Helper method to search counselings with date range handling
     */
    private List<Counseling> searchCounselingsWithDateRange(String searchTerm, LocalDateTime startDate,
                                                            LocalDateTime endDate, int limit, int offset) {
        if (startDate == null && endDate == null) {
            return counselingRepository.fullTextSearchWithPagination(searchTerm, limit, offset);
        }
        return counselingRepository.fullTextSearchWithPaginationAndDateRange(
                searchTerm, startDate, endDate, limit, offset);
    }

    /**
     * Helper method to search clients with date range handling
     */
    private List<Client> searchClientsWithDateRange(String searchTerm, LocalDateTime startDate,
                                                    LocalDateTime endDate, int limit, int offset) {
        if (startDate == null && endDate == null) {
            return clientRepository.fullTextSearchWithPagination(searchTerm, limit, offset);
        }
        return clientRepository.fullTextSearchWithPaginationAndDateRange(
                searchTerm, startDate, endDate, limit, offset);
    }

}
