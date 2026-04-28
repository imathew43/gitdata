package com.branchapp.gitdata.integration.github;

import com.branchapp.gitdata.error.TooManyRequestsException;
import com.branchapp.gitdata.integration.github.error.RateLimitException;
import com.branchapp.gitdata.integration.github.model.Repository;
import com.branchapp.gitdata.integration.github.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubService {
    private final GithubFeign feign;

    private static final int PAGE_SIZE = 100;
    private static final String LINK_HEADER = "link";
    private static final String NEXT_PARAM = "next";

    @Cacheable("users")
    public Optional<User> getUserByUsername(String username) {
        log.info("Querying github for username [{}]", username);
        try {
            return feign.getUserByUsername(username);
        } catch (RateLimitException rle) {
            throw convertRateLimit(rle);
        }
    }

    @Cacheable("repositories")
    public List<Repository> getRepositoriesForUsername(String username) {
        log.info("Querying github for repositories for username [{}]", username);
        List<Repository> repositoryList = new ArrayList<>();
        int page = 1;
        ResponseEntity<List<Repository>> activeResponse;
        do {
            activeResponse = getRepositoryPage(username, page);
            repositoryList.addAll(activeResponse.getBody());
            page++;
        } while (hasNext(activeResponse));

        return repositoryList;
    }

    private ResponseEntity<List<Repository>> getRepositoryPage(String username, int page) {
        try {
            return feign.getRepositoriesForUsername(username, PAGE_SIZE, page);
        } catch (RateLimitException rle) {
            throw convertRateLimit(rle);
        }
    }

    private boolean hasNext(ResponseEntity<?> response) {
        if (response == null) {
            return false;
        }
        return response.getHeaders().getOrEmpty(LINK_HEADER).stream().findFirst().orElse("").contains(NEXT_PARAM);
    }

    private RuntimeException convertRateLimit(RateLimitException rle) {
        return new TooManyRequestsException(rle.getMessage(), rle.getResetDate() - currentEpochTime());
    }

    private long currentEpochTime() {
        return System.currentTimeMillis() / 1000L;
    }
}
