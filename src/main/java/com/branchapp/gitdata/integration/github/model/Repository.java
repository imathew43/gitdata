package com.branchapp.gitdata.integration.github.model;

import lombok.Data;

/**
 * Github Repository Information
 *
 * A subset of the Github Core API Repository
 */
@Data
public class Repository {
    String name;

    String url;
}
