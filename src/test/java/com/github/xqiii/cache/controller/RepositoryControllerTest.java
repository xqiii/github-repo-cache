package com.github.xqiii.cache.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.xqiii.cache.dto.RepositoryResponse;
import com.github.xqiii.cache.service.RepositoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepositoryController.class)
class RepositoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetRepository_Success() throws Exception {
        // Given
        String owner = "spring-projects";
        String repositoryName = "spring-boot";
        RepositoryResponse response = new RepositoryResponse(
            "spring-projects/spring-boot",
            "Spring Boot",
            "https://github.com/spring-projects/spring-boot.git",
            50000,
            LocalDateTime.of(2020, 1, 1, 0, 0)
        );

        when(repositoryService.getRepositoryDetails(owner, repositoryName))
            .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/repositories/{owner}/{repositoryName}", owner, repositoryName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.fullName").value("spring-projects/spring-boot"))
                .andExpect(jsonPath("$.description").value("Spring Boot"))
                .andExpect(jsonPath("$.cloneUrl").value("https://github.com/spring-projects/spring-boot.git"))
                .andExpect(jsonPath("$.stars").value(50000))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void testGetRepository_NotFound() throws Exception {
        // Given
        String owner = "non-existent";
        String repositoryName = "repo";

        when(repositoryService.getRepositoryDetails(owner, repositoryName))
            .thenThrow(new RuntimeException("Repository not found: non-existent/repo"));

        // When & Then
        mockMvc.perform(get("/repositories/{owner}/{repositoryName}", owner, repositoryName)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

