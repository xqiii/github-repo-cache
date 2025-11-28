package com.github.xqiii.cache.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class GithubApiResponse {
    
    @JsonProperty("full_name")
    private String fullName;
    
    private String description;
    
    @JsonProperty("clone_url")
    private String cloneUrl;
    
    @JsonProperty("stargazers_count")
    private Integer stargazersCount;
    
    @JsonProperty("created_at")
    private String createdAt;
}

