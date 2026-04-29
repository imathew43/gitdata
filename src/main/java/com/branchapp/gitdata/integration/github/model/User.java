package com.branchapp.gitdata.integration.github.model;

import lombok.Data;
import java.time.ZonedDateTime;

/**
 * Github User Representation
 *
 * A subset of the User object provided by the Github Core API
 */
@Data
public class User {

    //user_name
    private String login;

    private String name;

    private String avatar_url;

    private String email;

    private String location;

    private String url;

    private ZonedDateTime created_at;

}
