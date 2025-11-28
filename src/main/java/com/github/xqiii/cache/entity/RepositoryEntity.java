package com.github.xqiii.cache.entity;

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
}

