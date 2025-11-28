package com.github.xqiii.cache.repository;

import com.github.xqiii.cache.entity.RepositoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryRepository extends JpaRepository<RepositoryEntity, Long> {
    
    Optional<RepositoryEntity> findByOwnerAndRepositoryName(String owner, String repositoryName);
}

