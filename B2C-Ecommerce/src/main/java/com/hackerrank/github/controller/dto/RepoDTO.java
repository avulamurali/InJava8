package com.hackerrank.github.controller.dto;

import com.hackerrank.github.model.Repo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepoDTO {
    private Long id;
    private String name;
    private String url;

    static RepoDTO convertFrom(Repo repo) {
        return new RepoDTO(repo.getId(), repo.getName(), repo.getUrl());
    }
}
