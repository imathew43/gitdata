package com.branchapp.gitdata.service;

import com.branchapp.gitdata.error.UserNotFoundException;
import com.branchapp.gitdata.integration.github.GithubService;
import com.branchapp.gitdata.integration.github.model.Repository;
import com.branchapp.gitdata.integration.github.model.User;
import com.branchapp.gitdata.mapper.GithubUserMapper;
import com.branchapp.gitdata.model.GithubUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubUserService {
    private final GithubService githubService;
    private final GithubUserMapper mapper;

    public GithubUser retrieveUser(String username) {
        log.info("Retrieving user information for username [{}]", username);
        User user = githubService.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        List<Repository> repositories = githubService.getRepositoriesForUsername(username);
        return mapper.convertUser(user, repositories);
    }
}
