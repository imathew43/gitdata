package com.branchapp.gitdata.controller;

import com.branchapp.gitdata.integration.github.GithubFeign;
import com.branchapp.gitdata.integration.github.GithubService;
import com.branchapp.gitdata.integration.github.error.RateLimitException;
import com.branchapp.gitdata.integration.github.model.Repository;
import com.branchapp.gitdata.integration.github.model.User;
import com.branchapp.gitdata.mapper.GithubUserMapper;
import com.branchapp.gitdata.service.GithubUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureCache
public class GithubUserControllerTest {

    @Autowired
    private GithubService githubService;

    @Autowired
    private GithubUserMapper githubUserMapper;

    @Autowired
    private GithubUserService githubUserService;

    @MockitoBean
    private GithubFeign githubFeign;

    @Autowired
    private GithubUserController controller;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void happyPath() throws Exception {
        User user = new User();
        user.setLogin("foo");
        user.setName("Foo User");
        when(githubFeign.getUserByUsername("foo")).thenReturn(Optional.of(user));

        ResponseEntity<List<Repository>> responseEntity = Mockito.mock();
        HttpHeaders headers = Mockito.mock();
        Repository repository = new Repository();
        repository.setName("foo repo");
        repository.setUrl("https://foo.com");

        when(githubFeign.getRepositoriesForUsername("foo", 100, 1)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(List.of(repository));
        when(responseEntity.getHeaders()).thenReturn(headers);
        when(headers.getOrEmpty("link")).thenReturn(List.of());

        mockMvc.perform(MockMvcRequestBuilders.get("/github/users/foo"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_name").value("foo"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.repos[0].name").value("foo repo"));

    }

    @Test
    void rateLimited() throws Exception {
        User user = new User();
        user.setLogin("foo");
        user.setName("Foo User");
        when(githubFeign.getUserByUsername("foo")).thenReturn(Optional.of(user));
        long testTime = (System.currentTimeMillis() / 1000L) + 200L;
        when(githubFeign.getRepositoriesForUsername("foo", 100, 1)).thenThrow(new RateLimitException(testTime));

        mockMvc.perform(MockMvcRequestBuilders.get("/github/users/foo"))
                .andExpect(MockMvcResultMatchers.status().isTooManyRequests())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Github rate limit exceeded"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(429));

    }

    @Test
    void notFound() throws Exception {
        when(githubFeign.getUserByUsername("foo")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/github/users/foo"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("User with username [foo] not found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(404));

    }

    @Test
    void unknownError() throws Exception {
        when(githubFeign.getUserByUsername("foo")).thenThrow(new RuntimeException("hi there"));

        mockMvc.perform(MockMvcRequestBuilders.get("/github/users/foo"))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Unexpected error occurred"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(500));

    }
}
