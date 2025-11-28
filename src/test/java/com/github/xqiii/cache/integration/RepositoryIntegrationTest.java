package com.github.xqiii.cache.integration;

import com.github.xqiii.cache.dto.GithubApiResponse;
import com.github.xqiii.cache.entity.RepositoryEntity;
import com.github.xqiii.cache.repository.RepositoryRepository;
import com.github.xqiii.cache.service.GithubApiService;
import com.github.xqiii.cache.service.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RepositoryIntegrationTest {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RepositoryRepository repositoryRepository;

    @MockBean
    private GithubApiService githubApiService;

    @BeforeEach
    void setUp() {
        repositoryRepository.deleteAll();
    }

    @Test
    void testGetRepository_FromCache() {
        // Given - Pre-save a repository in the database
        String owner = "test-owner";
        String repositoryName = "test-repo";
        RepositoryEntity cachedEntity = new RepositoryEntity(
            owner,
            repositoryName,
            "test-owner/test-repo",
            "Test repository",
            "https://github.com/test-owner/test-repo.git",
            100,
            LocalDateTime.now()
        );
        repositoryRepository.save(cachedEntity);

        // When
        var response = repositoryService.getRepositoryDetails(owner, repositoryName);

        // Then
        assertNotNull(response);
        assertEquals("test-owner/test-repo", response.getFullName());
        assertEquals("Test repository", response.getDescription());
        assertEquals(100, response.getStars());
        
        // Verify GitHub API was not called
        verify(githubApiService, never()).fetchRepositoryDetails(anyString(), anyString());
    }

    @Test
    void testGetRepository_FromGitHubAPI_ThenCache() {
        // Given - Mock GitHub API response
        String owner = "spring-projects";
        String repositoryName = "spring-boot";
        
        var githubResponse = new GithubApiResponse();
        githubResponse.setFullName("spring-projects/spring-boot");
        githubResponse.setDescription("Spring Boot Framework");
        githubResponse.setCloneUrl("https://github.com/spring-projects/spring-boot.git");
        githubResponse.setStargazersCount(50000);
        githubResponse.setCreatedAt("2020-01-01T00:00:00Z");

        when(githubApiService.fetchRepositoryDetails(owner, repositoryName))
            .thenReturn(githubResponse);

        // When - First call (cache miss)
        var response1 = repositoryService.getRepositoryDetails(owner, repositoryName);

        // Then - Verify response
        assertNotNull(response1);
        assertEquals("spring-projects/spring-boot", response1.getFullName());
        assertEquals("Spring Boot Framework", response1.getDescription());
        assertEquals(50000, response1.getStars());
        
        // Verify saved to database
        Optional<RepositoryEntity> savedEntity = repositoryRepository
            .findByOwnerAndRepositoryName(owner, repositoryName);
        assertTrue(savedEntity.isPresent());
        assertEquals("spring-projects/spring-boot", savedEntity.get().getFullName());

        // When - Second call (should get from cache)
        var response2 = repositoryService.getRepositoryDetails(owner, repositoryName);

        // Then - Verify same response
        assertEquals(response1.getFullName(), response2.getFullName());
        assertEquals(response1.getDescription(), response2.getDescription());
        assertEquals(response1.getStars(), response2.getStars());
        
        // Verify GitHub API was called only once
        verify(githubApiService, times(1)).fetchRepositoryDetails(owner, repositoryName);
    }
}

