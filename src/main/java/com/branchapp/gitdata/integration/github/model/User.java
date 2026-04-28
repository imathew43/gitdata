package com.branchapp.gitdata.integration.github.model;

import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class User {

    //user_name
    private String login;

    private String name; //display_name

    private String avatar_url; //avatar

    private String email;

    private String location; //geo_location

    private String url; //url

    private ZonedDateTime created_at;

}
