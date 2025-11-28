package com.github.xqiii.cache.service;

import com.github.xqiii.cache.dto.GithubApiResponse;
import com.github.xqiii.cache.dto.RepositoryResponse;
import com.github.xqiii.cache.entity.RepositoryEntity;
import com.github.xqiii.cache.repository.RepositoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
public class RepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryService.class);
    
    private final RepositoryRepository repositoryRepository;
    private final GithubApiService githubApiService;

    public RepositoryService(RepositoryRepository repositoryRepository, 
                            GithubApiService githubApiService) {
        this.repositoryRepository = repositoryRepository;
        this.githubApiService = githubApiService;
    }

    @Transactional
    public RepositoryResponse getRepositoryDetails(String owner, String repositoryName) {
        // First check cache
        Optional<RepositoryEntity> cachedEntity = repositoryRepository
            .findByOwnerAndRepositoryName(owner, repositoryName);
        
        if (cachedEntity.isPresent()) {
            logger.info("Repository found in cache: {}/{}", owner, repositoryName);
            return convertToResponse(cachedEntity.get());
        }
        
        // Cache miss, fetch from GitHub API
        logger.info("Cache miss, fetching from GitHub API: {}/{}", owner, repositoryName);
        GithubApiResponse githubResponse = githubApiService.fetchRepositoryDetails(owner, repositoryName);
        
        // Save to cache
        RepositoryEntity entity = convertToEntity(owner, repositoryName, githubResponse);
        RepositoryEntity savedEntity = repositoryRepository.save(entity);
        logger.info("Repository details cached: {}/{}", owner, repositoryName);
        
        return convertToResponse(savedEntity);
    }

    private RepositoryEntity convertToEntity(String owner, String repositoryName, 
                                           GithubApiResponse githubResponse) {
        LocalDateTime createdAt = parseGithubDate(githubResponse.getCreatedAt());
        
        return new RepositoryEntity(
            owner,
            repositoryName,
            githubResponse.getFullName(),
            githubResponse.getDescription(),
            githubResponse.getCloneUrl(),
            githubResponse.getStargazersCount() != null ? githubResponse.getStargazersCount() : 0,
            createdAt
        );
    }

    private RepositoryResponse convertToResponse(RepositoryEntity entity) {
        return new RepositoryResponse(
            entity.getFullName(),
            entity.getDescription(),
            entity.getCloneUrl(),
            entity.getStars(),
            entity.getCreatedAt()
        );
    }

    private LocalDateTime parseGithubDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            // GitHub API returns ISO 8601 formatted date-time string
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            logger.warn("Failed to parse date: {}, using current time", dateString);
            return LocalDateTime.now();
        }
    }
}

