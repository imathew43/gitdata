package com.branchapp.gitdata.controller;

import com.branchapp.gitdata.model.GithubUser;
import com.branchapp.gitdata.service.GithubUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("github/users")
@RequiredArgsConstructor
@Slf4j
public class GithubUserController {
    private final GithubUserService githubUserService;

    @GetMapping("/{username}")
    public GithubUser getUserInformation(@PathVariable String username)
    {
        log.info("Get user called for username [{}]", username);
        return githubUserService.retrieveUser(username);
    }

}
