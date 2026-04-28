package com.branchapp.gitdata.service;

import com.branchapp.gitdata.error.TooManyRequestsException;
import com.branchapp.gitdata.error.UserNotFoundException;
import com.branchapp.gitdata.integration.github.GithubService;
import com.branchapp.gitdata.integration.github.model.Repository;
import com.branchapp.gitdata.integration.github.model.User;
import com.branchapp.gitdata.mapper.GithubUserMapper;
import com.branchapp.gitdata.model.GithubUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GithubUserServiceTest {

    private final GithubService githubService = Mockito.mock();

    private final GithubUserMapper mapper = new GithubUserMapper();

    private final GithubUserService githubUserService = new GithubUserService(githubService, mapper);

    @Test
    public void testGetUserHappyPath() {
        User user = new User();
        user.setLogin("foo");
        user.setName("Foo User");
        Repository repository = new Repository();
        repository.setName("foo repo");
        repository.setUrl("https://foo.com");
        when(githubService.getUserByUsername("foo")).thenReturn(Optional.of(user));
        when(githubService.getRepositoriesForUsername("foo")).thenReturn(List.of(repository));

        GithubUser returnUser = githubUserService.retrieveUser("foo");
        verify(githubService, times(1)).getUserByUsername(eq("foo"));
        verify(githubService, times(1)).getRepositoriesForUsername(eq("foo"));
        assertEquals("foo", returnUser.getUserName());
        assertEquals("Foo User", returnUser.getDisplayName());
        assertEquals(1, returnUser.getRepos().size());
        assertEquals("foo repo", returnUser.getRepos().get(0).getName());
    }

    @Test
    public void testGetUserNotFound() {
        when(githubService.getUserByUsername("foo")).thenReturn(Optional.empty());
        when(githubService.getRepositoriesForUsername("foo")).thenReturn(List.of());

        try {
            githubUserService.retrieveUser("foo");
            fail("Exception should have been thrown for not found user");
        } catch (UserNotFoundException unfe) {
            assertEquals("User with username [foo] not found", unfe.getMessage());
        }
        verify(githubService, times(1)).getUserByUsername(eq("foo"));
        verify(githubService, times(0)).getRepositoriesForUsername(anyString());
    }

    @Test
    public void testGetUserHitsRateLimit() {
        User user = new User();
        user.setLogin("foo");
        user.setName("Foo User");
        when(githubService.getUserByUsername("foo")).thenReturn(Optional.of(user));
        when(githubService.getRepositoriesForUsername("foo")).thenThrow(new TooManyRequestsException("GitHub RateLimit exceeded", 0));

        try {
            GithubUser returnUser = githubUserService.retrieveUser("foo");
            fail("Exception should have been thrown for too many requests");
        } catch (TooManyRequestsException tmre) {
            assertEquals(1, tmre.getDelayInSeconds());
        }
        verify(githubService, times(1)).getUserByUsername(eq("foo"));
        verify(githubService, times(1)).getRepositoriesForUsername(eq("foo"));
    }
}
