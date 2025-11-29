package com.github.xqiii.cache.entity;

import com.github.xqiii.cache.dto.GithubApiResponse;
import com.github.xqiii.cache.dto.RepositoryResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Repository entity
 * 
 * @author xiuqiii
 * @date 2025-11-29
 */
@Entity
@Table(name = "repositories", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"owner", "repositoryName"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private String repositoryName;

    @Column(nullable = false)
    private String fullName;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String cloneUrl;

    @Column(nullable = false)
    private Integer stars;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public RepositoryEntity(String owner, String repositoryName, String fullName, 
                           String description, String cloneUrl, Integer stars, 
                           LocalDateTime createdAt) {
        this.owner = owner;
        this.repositoryName = repositoryName;
        this.fullName = fullName;
        this.description = description;
        this.cloneUrl = cloneUrl;
        this.stars = stars;
        this.createdAt = createdAt;
    }

    /**
     * Create RepositoryEntity from GithubApiResponse
     */
    public static RepositoryEntity fromGithubApiResponse(String owner, String repositoryName, GithubApiResponse githubResponse) {
        RepositoryEntity entity = new RepositoryEntity();
        entity.setOwner(owner);
        entity.setRepositoryName(repositoryName);
        entity.setFullName(githubResponse.getFullName());
        entity.setDescription(githubResponse.getDescription());
        entity.setCloneUrl(githubResponse.getCloneUrl());
        entity.setStars(stargazersCountToStars(githubResponse.getStargazersCount()));
        entity.setCreatedAt(stringToLocalDateTime(githubResponse.getCreatedAt()));
        return entity;
    }

    /**
     * Convert RepositoryEntity to RepositoryResponse
     */
    public RepositoryResponse toResponse() {
        RepositoryResponse response = new RepositoryResponse();
        response.setFullName(this.fullName);
        response.setDescription(this.description);
        response.setCloneUrl(this.cloneUrl);
        response.setStars(this.stars);
        response.setCreatedAt(this.createdAt);
        return response;
    }

    /**
     * Convert stargazers count to stars, default to 0 if null
     */
    private static Integer stargazersCountToStars(Integer stargazersCount) {
        return stargazersCount != null ? stargazersCount : 0;
    }

    /**
     * Convert string date to LocalDateTime
     */
    private static LocalDateTime stringToLocalDateTime(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return LocalDateTime.now();
        }
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString);
            return zonedDateTime.toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}

