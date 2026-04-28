package com.branchapp.gitdata.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class GithubUser {

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("display_name")
    private String displayName;

    @JsonProperty("avatar")
    private String avatar;

    @JsonProperty("geo_location")
    private String geolocation;

    @JsonProperty("email")
    private String email;

    @JsonProperty("url")
    private String url;

    @JsonProperty("created_at")
    @JsonFormat(pattern="EEE, dd MMM yyyy HH:mm:ss z", timezone="GMT")
    private ZonedDateTime createdAt;

    @JsonProperty("repos")
    private List<GithubRepo> repos;

}
