package com.github.xqiii.cache.controller;

import com.github.xqiii.cache.dto.RepositoryResponse;
import com.github.xqiii.cache.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repositories")
public class RepositoryController {

    private static final Logger logger = LoggerFactory.getLogger(RepositoryController.class);
    
    private final RepositoryService repositoryService;

    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @GetMapping("/{owner}/{repositoryName}")
    public ResponseEntity<RepositoryResponse> getRepository(
            @PathVariable String owner,
            @PathVariable String repositoryName) {
        
        logger.info("Received request for repository: {}/{}", owner, repositoryName);
        
        try {
            RepositoryResponse response = repositoryService.getRepositoryDetails(owner, repositoryName);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.error("Error processing request for {}/{}: {}", owner, repositoryName, e.getMessage());
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

