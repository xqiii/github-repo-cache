package com.github.xqiii.cache.service;

import com.github.xqiii.cache.dto.GithubApiResponse;
import com.github.xqiii.cache.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

/**
 * GitHub API service
 * 
 * @author xiuqiii
 * @date 2025-11-29
 */
@Service
public class GithubApiService {

    private static final Logger logger = LoggerFactory.getLogger(GithubApiService.class);
    
    private final RestTemplate restTemplate;
    private final String githubApiBaseUrl;

    public GithubApiService(@Value("${github.api.base-url}") String githubApiBaseUrl,
                            @Value("${github.api.connect-timeout:5000}") int connectTimeout,
                            @Value("${github.api.read-timeout:10000}") int readTimeout) {
        this.restTemplate = new RestTemplate();
        this.githubApiBaseUrl = githubApiBaseUrl;
        
        // Configure timeout settings
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        this.restTemplate.setRequestFactory(factory);
    }

    public GithubApiResponse fetchRepositoryDetails(String owner, String repositoryName) {
        String url = String.format("%s/repos/%s/%s", githubApiBaseUrl, owner, repositoryName);
        
        logger.info("Fetching repository details from GitHub API: {}", url);
        
        try {
            ResponseEntity<GithubApiResponse> response = restTemplate.getForEntity(
                url, 
                GithubApiResponse.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("Successfully fetched repository details for {}/{}", owner, repositoryName);
                return response.getBody();
            } else {
                logger.error("Unexpected response from GitHub API: {}", response.getStatusCode());
                throw new BizException(
                    "GITHUB_API_ERROR",
                    "Failed to fetch repository details from GitHub API",
                    HttpStatus.INTERNAL_SERVER_ERROR.value()
                );
            }
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Repository not found: {}/{}", owner, repositoryName);
            throw new BizException(
                "REPOSITORY_NOT_FOUND",
                "Repository not found: " + owner + "/" + repositoryName,
                HttpStatus.NOT_FOUND.value(),
                e
            );
        } catch (RestClientException e) {
            logger.error("Error calling GitHub API: {}", e.getMessage(), e);
            throw new BizException(
                "GITHUB_API_ERROR",
                "Error calling GitHub API: " + e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e
            );
        }
    }
}

