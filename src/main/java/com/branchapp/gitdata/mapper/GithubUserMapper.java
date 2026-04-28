package com.branchapp.gitdata.mapper;

import com.branchapp.gitdata.integration.github.model.Repository;
import com.branchapp.gitdata.integration.github.model.User;
import com.branchapp.gitdata.model.GithubRepo;
import com.branchapp.gitdata.model.GithubUser;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GithubUserMapper {
    public GithubUser convertUser(User user, List<Repository> repositories) {

        List<GithubRepo> repos = repositories.stream().map(this::convertRepo).toList();

        return GithubUser.builder()
                .avatar(user.getAvatar_url())
                .email(user.getEmail())
                .url(user.getUrl())
                .userName(user.getLogin())
                .displayName(user.getName())
                .createdAt(user.getCreated_at())
                .geolocation(user.getLocation())
                .repos(repos)
                .build();
    }

    private GithubRepo convertRepo(Repository repository) {
        return new GithubRepo(repository.getName(), repository.getUrl());
    }
}
