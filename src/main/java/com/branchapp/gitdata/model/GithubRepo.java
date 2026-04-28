package com.branchapp.gitdata.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GithubRepo {
    private String name;

    private String url;

}
