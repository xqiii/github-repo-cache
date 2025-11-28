package com.github.xqiii.cache.service;

import com.github.xqiii.cache.dto.GithubApiResponse;
import com.github.xqiii.cache.dto.RepositoryResponse;
import com.github.xqiii.cache.entity.RepositoryEntity;
import com.github.xqiii.cache.mapper.RepositoryMapper;
import com.github.xqiii.cache.repository.RepositoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RepositoryService {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryService.class);
    
    private final RepositoryRepository repositoryRepository;
    private final GithubApiService githubApiService;
    private final RepositoryMapper repositoryMapper;

    public RepositoryService(RepositoryRepository repositoryRepository, 
                            GithubApiService githubApiService,
                            RepositoryMapper repositoryMapper) {
        this.repositoryRepository = repositoryRepository;
        this.githubApiService = githubApiService;
        this.repositoryMapper = repositoryMapper;
    }

    @Transactional
    public RepositoryResponse getRepositoryDetails(String owner, String repositoryName) {
        // First check cache
        Optional<RepositoryEntity> cachedEntity = repositoryRepository
            .findByOwnerAndRepositoryName(owner, repositoryName);
        
        if (cachedEntity.isPresent()) {
            logger.info("Repository found in cache: {}/{}", owner, repositoryName);
            return repositoryMapper.toResponse(cachedEntity.get());
        }
        
        // Cache miss, fetch from GitHub API
        logger.info("Cache miss, fetching from GitHub API: {}/{}", owner, repositoryName);
        GithubApiResponse githubResponse = githubApiService.fetchRepositoryDetails(owner, repositoryName);
        
        // Save to cache
        RepositoryEntity entity = repositoryMapper.toEntity(owner, repositoryName, githubResponse);
        RepositoryEntity savedEntity = repositoryRepository.save(entity);
        logger.info("Repository details cached: {}/{}", owner, repositoryName);
        
        return repositoryMapper.toResponse(savedEntity);
    }
}

