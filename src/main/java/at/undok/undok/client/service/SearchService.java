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
import at.undok.undok.client.util.CategoryType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SearchService {

    /**
     * Category types to include in client search.
     * Add additional types here to extend the search (e.g., "COUNSELING_LANGUAGE", "ORIGIN_OF_ATTENTION").
     * Only types stored in join_category are searchable, and only where the entity they hang off
     * is covered by the queries in ClientRepo (currently CASE and COUNSELING).
     */
    private static final List<String> SEARCHABLE_CATEGORY_TYPES = List.of(
            CategoryType.INDUSTRY_UNION,
            CategoryType.SECTOR,
            CategoryType.ACTIVITY,
            CategoryType.AUFENTHALTSTITEL);

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

    public UnifiedSearchResponse searchAll(String searchTerm, LocalDateTime startDate,
                                           LocalDateTime endDate, int page, int size) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return emptyResponse(page, size);
        }
        String term = prepareSearchTerm(searchTerm.trim());
        boolean hasDates = startDate != null || endDate != null;

        long totalCounselings = hasDates
                ? counselingRepository.countFullTextSearchWithDateRange(term, startDate, endDate)
                : counselingRepository.countFullTextSearch(term);
        long totalClients = hasDates
                ? countMergedClientsWithDateRange(term, startDate, endDate)
                : countMergedClients(term);
        long totalTasks = hasDates
                ? taskRepo.countFullTextSearchWithDateRange(term, startDate, endDate)
                : taskRepo.countFullTextSearch(term);

        return buildPagedResponse(page, size, totalCounselings, totalClients, totalTasks,
                (limit, offset) -> hasDates
                        ? counselingRepository.fullTextSearchWithPaginationAndDateRange(term, startDate, endDate, limit, offset)
                        : counselingRepository.fullTextSearchWithPagination(term, limit, offset),
                (limit, offset) -> hasDates
                        ? getMergedClientsWithDateRangeAndPagination(term, startDate, endDate, limit, offset)
                        : getMergedClientsWithPagination(term, limit, offset),
                (limit, offset) -> hasDates
                        ? taskRepo.fullTextSearchWithPaginationAndDateRange(term, startDate, endDate, limit, offset)
                        : taskRepo.fullTextSearchWithPagination(term, limit, offset),
                term);
    }

    public UnifiedSearchResponse searchAll(String searchTerm, int page, int size) {
        return searchAll(searchTerm, null, null, page, size);
    }

    private UnifiedSearchResponse emptyResponse(int page, int size) {
        return new UnifiedSearchResponse(List.of(), List.of(), List.of(),
                new PaginationInfo(page, size, 0, 0, 0));
    }

    private UnifiedSearchResponse buildPagedResponse(
            int page, int size,
            long totalCounselings, long totalClients, long totalTasks,
            BiFunction<Integer, Integer, List<Counseling>> fetchCounselings,
            BiFunction<Integer, Integer, List<Client>> fetchClients,
            BiFunction<Integer, Integer, List<Task>> fetchTasks,
            String searchTerm) {

        long totalElements = totalCounselings + totalClients + totalTasks;
        int offset = page * size;

        List<CounselingSearchResult> counselingDtos = new ArrayList<>();
        List<ClientSearchResult> clientDtos = new ArrayList<>();
        List<TaskSearchResult> taskDtos = new ArrayList<>();

        if (offset < totalElements) {
            int remaining = size;

            if (offset < totalCounselings) {
                int toFetch = (int) Math.min(remaining, totalCounselings - offset);
                counselingDtos = fetchCounselings.apply(toFetch, offset).stream()
                        .map(CounselingSearchResult::new).collect(Collectors.toList());
                remaining -= counselingDtos.size();
                offset = 0;
            } else {
                offset -= (int) totalCounselings;
            }

            if (remaining > 0 && offset < totalClients) {
                int toFetch = (int) Math.min(remaining, totalClients - offset);
                Map<UUID, List<MatchedCategoryResult>> matchedCategories = getMatchedCategoriesMap(searchTerm);
                clientDtos = fetchClients.apply(toFetch, offset).stream()
                        .map(c -> new ClientSearchResult(c, matchedCategories.get(c.getId())))
                        .collect(Collectors.toList());
                remaining -= clientDtos.size();
                offset = 0;
            } else if (remaining > 0) {
                offset -= (int) totalClients;
            }

            if (remaining > 0 && offset < totalTasks) {
                int toFetch = (int) Math.min(remaining, totalTasks - offset);
                taskDtos = fetchTasks.apply(toFetch, offset).stream()
                        .map(TaskSearchResult::new).collect(Collectors.toList());
            }
        }

        return new UnifiedSearchResponse(counselingDtos, clientDtos, taskDtos,
                new PaginationInfo(page, size, totalCounselings, totalClients, totalTasks));
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
     * Splits a prepared search term back into individual tokens for LIKE-based category search.
     * Tokens are separated by " or " (the websearch_to_tsquery OR operator).
     * Quotes are stripped from phrase tokens.
     * Example: "\"foo bar\" or baz"  →  ["foo bar", "baz"]
     */
    private List<String> categoryTokens(String preparedTerm) {
        return Arrays.stream(preparedTerm.split("(?i)\\s+or\\s+"))
                     .map(String::trim)
                     .map(t -> t.startsWith("\"") && t.endsWith("\"") ? t.substring(1, t.length() - 1) : t)
                     .filter(t -> !t.isEmpty())
                     .collect(Collectors.toList());
    }

    /**
     * Converts a search term so that unquoted words are OR-ed together using
     * websearch_to_tsquery's "or" operator. Quoted phrases are kept as-is.
     * Examples:
     *   "foo bar"         → "foo or bar"
     *   "\"foo bar\""     → "\"foo bar\""   (phrase, unchanged)
     *   "\"foo bar\" baz" → "\"foo bar\" or baz"
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
        return String.join(" or ", tokens);
    }

    /**
     * Get a map of client IDs to their matched categories (name + type)
     */
    private Map<UUID, List<MatchedCategoryResult>> getMatchedCategoriesMap(String searchTerm) {
        // Use a set per client to avoid adding the same category multiple times
        // when several tokens from a multi-word search match the same category.
        Map<UUID, LinkedHashSet<MatchedCategoryResult>> seen = new HashMap<>();
        for (String token : categoryTokens(searchTerm)) {
            List<ClientRepo.ClientCategoryMatch> matches = clientRepository.findMatchedCategoriesForClients(
                    token, SEARCHABLE_CATEGORY_TYPES);
            for (ClientRepo.ClientCategoryMatch match : matches) {
                seen.computeIfAbsent(match.getClientId(), k -> new LinkedHashSet<>())
                    .add(new MatchedCategoryResult(match.getCategoryName(), match.getCategoryType()));
            }
        }
        Map<UUID, List<MatchedCategoryResult>> result = new HashMap<>();
        seen.forEach((id, set) -> result.put(id, new ArrayList<>(set)));
        return result;
    }

}
