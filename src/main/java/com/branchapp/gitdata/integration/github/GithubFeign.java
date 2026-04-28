package com.branchapp.gitdata.integration.github;

import com.branchapp.gitdata.integration.github.error.GithubFeignErrorDecoder;
import com.branchapp.gitdata.integration.github.model.Repository;
import com.branchapp.gitdata.integration.github.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@FeignClient(value = "github", url = "https://api.github.com", configuration = {GithubFeignErrorDecoder.class}, dismiss404=true)
@Component
public interface GithubFeign {

    @GetMapping("/users/{username}")
    Optional<User> getUserByUsername(@PathVariable String username);

    @GetMapping("/users/{username}/repos")
    ResponseEntity<List<Repository>> getRepositoriesForUsername(@PathVariable String username, @RequestParam("per_page") int perPage, @RequestParam("page") int pageNum);
}
