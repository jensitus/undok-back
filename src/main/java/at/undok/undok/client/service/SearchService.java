package at.undok.undok.client.service;

import at.undok.undok.client.mapper.inter.ClientMapper;
import at.undok.undok.client.mapper.inter.CounselingMapper;
import at.undok.undok.client.model.dto.*;
import at.undok.undok.client.model.entity.Client;
import at.undok.undok.client.model.entity.Counseling;
import at.undok.undok.client.model.entity.Task;
import at.undok.undok.client.repository.ClientRepo;
import at.undok.undok.client.repository.CounselingRepo;
import at.undok.undok.client.repository.TaskRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchService {

    /**
     * Category types to include in client search.
     * Add additional types here to extend the search (e.g., "COUNSELING_LANGUAGE", "ORIGIN_OF_ATTENTION")
     */
    private static final List<String> SEARCHABLE_CATEGORY_TYPES = List.of("INDUSTRY_UNION", "SECTOR", "ACTIVITY");

    private final CounselingRepo counselingRepository;
    private final ClientRepo clientRepository;
    private final CounselingMapper counselingMapper;
    private final ClientMapper clientMapper;
    private final TaskRepo taskRepo;

    public SearchService(CounselingRepo counselingRepository,
                         ClientRepo clientRepository, CounselingMapper counselingMapper, ClientMapper clientMapper, TaskRepo taskRepo) {
        this.counselingRepository = counselingRepository;
        this.clientRepository = clientRepository;
        this.counselingMapper = counselingMapper;
        this.clientMapper = clientMapper;
        this.taskRepo = taskRepo;
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
            PaginationInfo emptyPagination = new PaginationInfo(page, size, 0, 0, 0);
            return new UnifiedSearchResponse(List.of(), List.of(), List.of(), emptyPagination);
        }

        String trimmedSearch = prepareSearchTerm(searchTerm.trim());

        // If no date range specified, use the simpler method without date filtering
        if (startDate == null && endDate == null) {
            return searchAll(trimmedSearch, page, size);
        }

        // Get total counts for pagination metadata (including category search for clients)
        long totalCounselings = countCounselingsWithDateRange(trimmedSearch, startDate, endDate);
        long totalClients = countMergedClientsWithDateRange(trimmedSearch, startDate, endDate);
        long totalTasks = countTasksWithDateRange(trimmedSearch, startDate, endDate);
        long totalElements = totalCounselings + totalClients + totalTasks;

        // Calculate how many results from each table for this page
        int offset = page * size;

        List<CounselingSearchResult> counselingDtos = new ArrayList<>();
        List<ClientSearchResult> clientDtos = new ArrayList<>();
        List<TaskSearchResult> taskDtos = new ArrayList<>();

        if (offset < totalElements) {
            int remaining = size;

            // 1. First, try to fill from counselings
            if (offset < totalCounselings) {
                int counselingsToFetch = (int) Math.min(remaining, totalCounselings - offset);
                List<Counseling> counselingResults = searchCounselingsWithDateRange(
                        trimmedSearch, startDate, endDate, counselingsToFetch, offset);
                counselingDtos = counselingResults.stream()
                                                  .map(CounselingSearchResult::new)
                                                  .collect(Collectors.toList());
                remaining -= counselingDtos.size();
                offset = 0; // Reset offset for next table
            } else {
                offset -= (int) totalCounselings; // Adjust offset for next table
            }

            // 2. Then, try to fill remaining space with clients (merged from fulltext + category search)
            if (remaining > 0 && offset < totalClients) {
                int clientsToFetch = (int) Math.min(remaining, totalClients - offset);
                List<Client> clientResults = getMergedClientsWithDateRangeAndPagination(
                        trimmedSearch, startDate, endDate, clientsToFetch, offset);
                Map<UUID, List<MatchedCategoryResult>> matchedCategories = getMatchedCategoriesMap(trimmedSearch);
                clientDtos = clientResults.stream()
                                          .map(c -> new ClientSearchResult(c, matchedCategories.get(c.getId())))
                                          .collect(Collectors.toList());
                remaining -= clientDtos.size();
                offset = 0; // Reset offset for next table
            } else if (remaining > 0) {
                offset -= (int) totalClients; // Adjust offset for next table
            }

            // 3. Finally, fill remaining space with tasks
            if (remaining > 0 && offset < totalTasks) {
                int tasksToFetch = (int) Math.min(remaining, totalTasks - offset);
                List<Task> taskResults = searchTasksWithDateRange(
                        trimmedSearch, startDate, endDate, tasksToFetch, offset);
                taskDtos = taskResults.stream()
                                      .map(TaskSearchResult::new)
                                      .collect(Collectors.toList());
            }
        }

        PaginationInfo pagination = new PaginationInfo(page, size, totalCounselings, totalClients, totalTasks);
        return new UnifiedSearchResponse(counselingDtos, clientDtos, taskDtos, pagination);
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
            PaginationInfo emptyPagination = new PaginationInfo(page, size, 0, 0, 0);
            return new UnifiedSearchResponse(List.of(), List.of(), List.of(), emptyPagination);
        }

        String trimmedSearch = prepareSearchTerm(searchTerm.trim());

        // Get total counts for pagination metadata (including category search for clients)
        long totalCounselings = counselingRepository.countFullTextSearch(trimmedSearch);
        long totalClients = countMergedClients(trimmedSearch);
        long totalTasks = taskRepo.countFullTextSearch(trimmedSearch);
        long totalElements = totalCounselings + totalClients + totalTasks;

        // Calculate how many results from each table for this page
        int offset = page * size;

        List<CounselingSearchResult> counselingDtos = new ArrayList<>();
        List<ClientSearchResult> clientDtos = new ArrayList<>();
        List<TaskSearchResult> taskDtos = new ArrayList<>();

        if (offset < totalElements) {
            int remaining = size;

            // 1. First, try to fill from counselings
            if (offset < totalCounselings) {
                int counselingsToFetch = (int) Math.min(remaining, totalCounselings - offset);
                List<Counseling> counselingResults = counselingRepository
                        .fullTextSearchWithPagination(trimmedSearch, counselingsToFetch, offset);
                counselingDtos = counselingResults.stream()
                                                  .map(CounselingSearchResult::new)
                                                  .collect(Collectors.toList());
                remaining -= counselingDtos.size();
                offset = 0; // Reset offset for next table
            } else {
                offset -= (int) totalCounselings; // Adjust offset for next table
            }

            // 2. Then, try to fill remaining space with clients (merged from fulltext + category search)
            if (remaining > 0 && offset < totalClients) {
                int clientsToFetch = (int) Math.min(remaining, totalClients - offset);
                List<Client> clientResults = getMergedClientsWithPagination(trimmedSearch, clientsToFetch, offset);
                Map<UUID, List<MatchedCategoryResult>> matchedCategories = getMatchedCategoriesMap(trimmedSearch);
                clientDtos = clientResults.stream()
                                          .map(c -> new ClientSearchResult(c, matchedCategories.get(c.getId())))
                                          .collect(Collectors.toList());
                remaining -= clientDtos.size();
                offset = 0; // Reset offset for next table
            } else if (remaining > 0) {
                offset -= (int) totalClients; // Adjust offset for next table
            }

            // 3. Finally, fill remaining space with tasks
            if (remaining > 0 && offset < totalTasks) {
                int tasksToFetch = (int) Math.min(remaining, totalTasks - offset);
                List<Task> taskResults = taskRepo
                        .fullTextSearchWithPagination(trimmedSearch, tasksToFetch, offset);
                taskDtos = taskResults.stream()
                                      .map(TaskSearchResult::new)
                                      .collect(Collectors.toList());
            }
        }

        PaginationInfo pagination = new PaginationInfo(page, size, totalCounselings, totalClients, totalTasks);
        return new UnifiedSearchResponse(counselingDtos, clientDtos, taskDtos, pagination);
    }

    /**
     * Unified search without pagination - returns all results
     *
     * @param searchTerm the search query
     * @return unified response containing all results from both tables
     */
    public UnifiedSearchResponse searchAll(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            PaginationInfo emptyPagination = new PaginationInfo(0, 0, 0, 0, 0);
            return new UnifiedSearchResponse(List.of(), List.of(), List.of(), emptyPagination);
        }

        String trimmedSearch = prepareSearchTerm(searchTerm.trim());

        // Search all three tables (clients merged from fulltext + category search)
        List<Counseling> counselingResults = counselingRepository.fullTextSearch(trimmedSearch);
        List<Client> clientResults = getMergedClients(trimmedSearch);
        List<Task> taskResults = taskRepo.fullTextSearch(trimmedSearch);
        Map<UUID, List<MatchedCategoryResult>> matchedCategories = getMatchedCategoriesMap(trimmedSearch);

        // Convert to DTOs
        List<CounselingSearchResult> counselingDtos = counselingResults.stream()
                                                                       .map(CounselingSearchResult::new)
                                                                       .collect(Collectors.toList());

        List<ClientSearchResult> clientDtos = clientResults.stream()
                                                           .map(c -> new ClientSearchResult(c, matchedCategories.get(c.getId())))
                                                           .collect(Collectors.toList());

        List<TaskSearchResult> taskDtos = taskResults.stream()
                                                     .map(TaskSearchResult::new)
                                                     .collect(Collectors.toList());

        long totalCounselings = counselingResults.size();
        long totalClients = clientResults.size();
        long totalTasks = taskResults.size();
        long totalElements = totalCounselings + totalClients + totalTasks;

        PaginationInfo pagination = new PaginationInfo(0, (int) totalElements,
                                                       totalCounselings, totalClients, totalTasks);

        return new UnifiedSearchResponse(counselingDtos, clientDtos, taskDtos, pagination);
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
     * Also searches clients connected to INDUSTRY_UNION categories matching the term
     *
     * @param searchTerm the search query (e.g., "John Smith", "keyword:urgent")
     * @return list of matching clients ordered by relevance
     */
    public List<ClientDto> searchClients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        return getMergedClients(searchTerm.trim()).stream().map(clientMapper::toDto).toList();
    }

    /**
     * Search clients with pagination
     * Also searches clients connected to INDUSTRY_UNION categories matching the term
     */
    public List<ClientDto> searchClients(String searchTerm, int page, int size) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }
        int offset = page * size;
        return getMergedClientsWithPagination(searchTerm.trim(), size, offset)
                               .stream()
                               .map(clientMapper::toDto)
                               .toList();
    }

    /**
     * Count matching clients for pagination
     * Includes both fulltext and INDUSTRY_UNION category matches
     */
    public long countClientSearchResults(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return 0;
        }
        return countMergedClients(searchTerm.trim());
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

    private long countTasksWithDateRange(String searchTerm, LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return taskRepo.countFullTextSearch(searchTerm);
        }
        return taskRepo.countFullTextSearchWithDateRange(searchTerm, startDate, endDate);
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

    private List<Task> searchTasksWithDateRange(String searchTerm, LocalDateTime startDate, LocalDateTime endDate,
                                                int limit, int offset) {
        if (startDate == null && endDate == null) {
            return taskRepo.fullTextSearchWithPagination(searchTerm, limit, offset);
        }
        return taskRepo.fullTextSearchWithPaginationAndDateRange(searchTerm, startDate, endDate, limit, offset);
    }

    /**
     * Merge fulltext search results with category search results.
     * Searches across all category types defined in SEARCHABLE_CATEGORY_TYPES.
     * Removes duplicates while preserving order (fulltext results first).
     */
    private List<Client> getMergedClients(String searchTerm) {
        List<Client> fulltextResults = clientRepository.fullTextSearch(searchTerm);

        Set<UUID> seenIds = new LinkedHashSet<>();
        List<Client> merged = new ArrayList<>();

        for (Client client : fulltextResults) {
            if (seenIds.add(client.getId())) merged.add(client);
        }
        for (String token : categoryTokens(searchTerm)) {
            for (Client client : clientRepository.findClientsByCategoryNames(token, SEARCHABLE_CATEGORY_TYPES)) {
                if (seenIds.add(client.getId())) merged.add(client);
            }
        }

        return merged;
    }

    /**
     * Merge fulltext search results with category search results with pagination
     */
    private List<Client> getMergedClientsWithPagination(String searchTerm, int limit, int offset) {
        // For pagination, we need to get all merged results first, then paginate
        List<Client> allMerged = getMergedClients(searchTerm);

        // Apply pagination
        int fromIndex = Math.min(offset, allMerged.size());
        int toIndex = Math.min(offset + limit, allMerged.size());

        return allMerged.subList(fromIndex, toIndex);
    }

    /**
     * Count merged clients from fulltext and category search
     */
    private long countMergedClients(String searchTerm) {
        Set<UUID> uniqueIds = new LinkedHashSet<>();
        clientRepository.fullTextSearch(searchTerm).forEach(c -> uniqueIds.add(c.getId()));
        for (String token : categoryTokens(searchTerm)) {
            clientRepository.findClientsByCategoryNames(token, SEARCHABLE_CATEGORY_TYPES)
                            .forEach(c -> uniqueIds.add(c.getId()));
        }
        return uniqueIds.size();
    }

    /**
     * Count merged clients from fulltext and category search with date range filter
     */
    private long countMergedClientsWithDateRange(String searchTerm, LocalDateTime startDate, LocalDateTime endDate) {
        List<Client> fulltextResults = (startDate == null && endDate == null)
                ? clientRepository.fullTextSearch(searchTerm)
                : clientRepository.fullTextSearchWithDateRange(searchTerm, startDate, endDate);

        Set<UUID> uniqueIds = new LinkedHashSet<>();
        fulltextResults.forEach(c -> uniqueIds.add(c.getId()));
        for (String token : categoryTokens(searchTerm)) {
            List<Client> categoryResults = (startDate == null && endDate == null)
                    ? clientRepository.findClientsByCategoryNames(token, SEARCHABLE_CATEGORY_TYPES)
                    : clientRepository.findClientsByCategoryNamesWithDateRange(token, SEARCHABLE_CATEGORY_TYPES, startDate, endDate);
            categoryResults.forEach(c -> uniqueIds.add(c.getId()));
        }
        return uniqueIds.size();
    }

    /**
     * Merge fulltext search results (with date range) with category search results
     */
    private List<Client> getMergedClientsWithDateRange(String searchTerm, LocalDateTime startDate, LocalDateTime endDate) {
        List<Client> fulltextResults = (startDate == null && endDate == null)
                ? clientRepository.fullTextSearch(searchTerm)
                : clientRepository.fullTextSearchWithDateRange(searchTerm, startDate, endDate);

        Set<UUID> seenIds = new LinkedHashSet<>();
        List<Client> merged = new ArrayList<>();

        for (Client client : fulltextResults) {
            if (seenIds.add(client.getId())) merged.add(client);
        }
        for (String token : categoryTokens(searchTerm)) {
            List<Client> categoryResults = (startDate == null && endDate == null)
                    ? clientRepository.findClientsByCategoryNames(token, SEARCHABLE_CATEGORY_TYPES)
                    : clientRepository.findClientsByCategoryNamesWithDateRange(token, SEARCHABLE_CATEGORY_TYPES, startDate, endDate);
            for (Client client : categoryResults) {
                if (seenIds.add(client.getId())) merged.add(client);
            }
        }

        return merged;
    }

    /**
     * Merge fulltext search results (with date range) with category search results, with pagination
     */
    private List<Client> getMergedClientsWithDateRangeAndPagination(String searchTerm, LocalDateTime startDate,
                                                                     LocalDateTime endDate, int limit, int offset) {
        List<Client> allMerged = getMergedClientsWithDateRange(searchTerm, startDate, endDate);

        // Apply pagination
        int fromIndex = Math.min(offset, allMerged.size());
        int toIndex = Math.min(offset + limit, allMerged.size());

        return allMerged.subList(fromIndex, toIndex);
    }

    /**
     * Splits a prepared search term (which may contain " | " separators) back into
     * individual tokens for use with LIKE-based category search.
     * Quotes are stripped from phrase tokens.
     * Example: "\"foo bar\" | baz"  →  ["foo bar", "baz"]
     */
    private List<String> categoryTokens(String preparedTerm) {
        return Arrays.stream(preparedTerm.split("\\s*\\|\\s*"))
                     .map(String::trim)
                     .map(t -> t.startsWith("\"") && t.endsWith("\"") ? t.substring(1, t.length() - 1) : t)
                     .filter(t -> !t.isEmpty())
                     .collect(Collectors.toList());
    }

    /**
     * Converts a search term so that unquoted words are OR-ed together.
     * Quoted phrases (e.g. "hello world") are kept as-is and treated as exact phrases.
     * Examples:
     *   "foo bar"       → "foo | bar"
     *   "\"foo bar\""   → "\"foo bar\""   (phrase, unchanged)
     *   "\"foo bar\" baz" → "\"foo bar\" | baz"
     */
    private String prepareSearchTerm(String searchTerm) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = Pattern.compile("\"[^\"]+\"|\\S+").matcher(searchTerm.trim());
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        if (tokens.size() <= 1) {
            return searchTerm.trim();
        }
        return String.join(" | ", tokens);
    }

    /**
     * Get a map of client IDs to their matched categories (name + type)
     */
    private Map<UUID, List<MatchedCategoryResult>> getMatchedCategoriesMap(String searchTerm) {
        Map<UUID, List<MatchedCategoryResult>> result = new HashMap<>();
        for (String token : categoryTokens(searchTerm)) {
            List<ClientRepo.ClientCategoryMatch> matches = clientRepository.findMatchedCategoriesForClients(
                    token, SEARCHABLE_CATEGORY_TYPES);
            for (ClientRepo.ClientCategoryMatch match : matches) {
                result.computeIfAbsent(match.getClientId(), k -> new ArrayList<>())
                      .add(new MatchedCategoryResult(match.getCategoryName(), match.getCategoryType()));
            }
        }
        return result;
    }

}
