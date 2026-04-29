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

    /**
     * Retrieve github user information based on username.
     *
     * Returns a representation of the user and related repositories if user exists, or:
     * * 404 and an error representation if user not found.
     * * 429 and an error representation with retry-after header if github rate limit exceeded
     * * 500 and an error representation for unexpected issues
     *
     * @param username the username of the user to look up
     * @return a representation of the User and related Repository information if exists
     */
    @GetMapping("/{username}")
    public GithubUser getUserInformation(@PathVariable String username)
    {
        log.info("Get user called for username [{}]", username);
        return githubUserService.retrieveUser(username);
    }

}
