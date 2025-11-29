package com.github.xqiii.cache.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Repository response DTO
 * 
 * @author xiuqiii
 * @date 2025-11-29
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepositoryResponse {
    
    private String fullName;
    private String description;
    private String cloneUrl;
    private Integer stars;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}

