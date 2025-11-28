package com.github.xqiii.cache.mapper;

import com.github.xqiii.cache.dto.GithubApiResponse;
import com.github.xqiii.cache.dto.RepositoryResponse;
import com.github.xqiii.cache.entity.RepositoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface RepositoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "owner", target = "owner")
    @Mapping(source = "repositoryName", target = "repositoryName")
    @Mapping(source = "githubResponse.fullName", target = "fullName")
    @Mapping(source = "githubResponse.description", target = "description")
    @Mapping(source = "githubResponse.cloneUrl", target = "cloneUrl")
    @Mapping(source = "githubResponse.stargazersCount", target = "stars", qualifiedByName = "stargazersCountToStars")
    @Mapping(source = "githubResponse.createdAt", target = "createdAt", qualifiedByName = "stringToLocalDateTime")
    RepositoryEntity toEntity(String owner, String repositoryName, GithubApiResponse githubResponse);

    RepositoryResponse toResponse(RepositoryEntity entity);

    @Named("stargazersCountToStars")
    default Integer stargazersCountToStars(Integer stargazersCount) {
        return stargazersCount != null ? stargazersCount : 0;
    }

    @Named("stringToLocalDateTime")
    default LocalDateTime stringToLocalDateTime(String dateString) {
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

