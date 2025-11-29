package com.github.xqiii.cache.service;

import com.github.xqiii.cache.dto.GithubApiResponse;
import com.github.xqiii.cache.dto.RepositoryResponse;
import com.github.xqiii.cache.entity.RepositoryEntity;
import com.github.xqiii.cache.repository.RepositoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository service
 * 
 * @author xiuqiii
 * @date 2025-11-29
 */
@Service
public class RepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryService.class);
    
    @Autowired
    private RepositoryRepository repositoryRepository;
    
    @Autowired
    private GithubApiService githubApiService;

    @Transactional
    public RepositoryResponse getRepositoryDetails(String owner, String repositoryName) {
        // First check cache
        Optional<RepositoryEntity> cachedEntity = repositoryRepository
            .findByOwnerAndRepositoryName(owner, repositoryName);
        
        if (cachedEntity.isPresent()) {
            logger.info("Repository found in cache: {}/{}", owner, repositoryName);
            return cachedEntity.get().toResponse();
        }
        
        // Cache miss, fetch from GitHub API
        logger.info("Cache miss, fetching from GitHub API: {}/{}", owner, repositoryName);
        GithubApiResponse githubResponse = githubApiService.fetchRepositoryDetails(owner, repositoryName);
        
        // Save to cache
        RepositoryEntity entity = RepositoryEntity.fromGithubApiResponse(owner, repositoryName, githubResponse);
        RepositoryEntity savedEntity = repositoryRepository.save(entity);
        logger.info("Repository details cached: {}/{}", owner, repositoryName);
        
        return savedEntity.toResponse();
    }
}

