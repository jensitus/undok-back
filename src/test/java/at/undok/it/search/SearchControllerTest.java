package at.undok.it.search;

import at.undok.undok.client.controller.SearchController;
import at.undok.undok.client.model.dto.PaginationInfo;
import at.undok.undok.client.model.dto.UnifiedSearchResponse;
import at.undok.undok.client.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void shouldReturnSearchResults() throws Exception {
        UnifiedSearchResponse mockResponse = new UnifiedSearchResponse(
                List.of(), List.of(), new PaginationInfo(0, 20, 0, 0)
        );

        when(searchService.searchAll(anyString(), any(), any(), anyInt(), anyInt()))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/service/undok/search")
                                .param("q", "test")
                                .param("page", "0")
                                .param("size", "20"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.totalResults", is(0)));
    }

}
