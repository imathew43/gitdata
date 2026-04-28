package com.branchapp.gitdata.integration.github;

import com.branchapp.gitdata.error.TooManyRequestsException;
import com.branchapp.gitdata.integration.github.error.RateLimitException;
import com.branchapp.gitdata.integration.github.model.Repository;
import com.branchapp.gitdata.integration.github.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GithubServiceTest {

    GithubFeign githubFeign = Mockito.mock();
    GithubService githubService = new GithubService(githubFeign);

    @Test
    public void testGetUserNotFound() {
        when(githubFeign.getUserByUsername("foo")).thenReturn(Optional.empty());

        Optional<User> returnedUser = githubService.getUserByUsername("foo");

        verify(githubFeign, times(1)).getUserByUsername(eq("foo"));
        assert(returnedUser.isEmpty());
    }

    @Test
    public void testGetUserHappyPath() {
        User user = new User();
        user.setLogin("foo");
        user.setName("Foo User");

        when(githubFeign.getUserByUsername("foo")).thenReturn(Optional.of(user));

        Optional<User> returnedUser = githubService.getUserByUsername("foo");

        verify(githubFeign, times(1)).getUserByUsername(eq("foo"));
        assertEquals("foo", returnedUser.get().getLogin());
        assertEquals("Foo User", returnedUser.get().getName());
    }

    @Test
    public void testGetUserRateLimit() {
        long testTime = (System.currentTimeMillis() / 1000L) + 100L;
        when(githubFeign.getUserByUsername("foo")).thenThrow(new RateLimitException(testTime));

        try {
            githubService.getUserByUsername("foo");
            fail("Should have returned an exception");
        } catch (TooManyRequestsException tmre) {
            assertThat("Expiration time", tmre.getDelayInSeconds(),greaterThan(0L));
            assertThat("Expiration time", tmre.getDelayInSeconds(), lessThanOrEqualTo(100L));
        }

        verify(githubFeign, times(1)).getUserByUsername(eq("foo"));
    }

    @Test
    public void testGetRepositoriesHappyPath() {
        ResponseEntity<List<Repository>> responseEntity = Mockito.mock();
        HttpHeaders headers = Mockito.mock();
        Repository repository = new Repository();
        repository.setName("foo repo");
        repository.setUrl("https://foo.com");

        when(githubFeign.getRepositoriesForUsername("foo", 100, 1)).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(List.of(repository));
        when(responseEntity.getHeaders()).thenReturn(headers);
        when(headers.getOrEmpty("link")).thenReturn(List.of());

        List<Repository> returnedRepositories = githubService.getRepositoriesForUsername("foo");

        verify(githubFeign, times(1)).getRepositoriesForUsername(anyString(), anyInt(), anyInt());

        assertEquals(1, returnedRepositories.size());
        assertEquals("foo repo", returnedRepositories.get(0).getName());
    }

    @Test
    public void testGetRepositoriesPaging() {
        ResponseEntity<List<Repository>> responseEntity1 = Mockito.mock();
        HttpHeaders headers1 = Mockito.mock();
        Repository repository1 = new Repository();
        repository1.setName("foo repo");
        repository1.setUrl("https://foo.com");

        ResponseEntity<List<Repository>> responseEntity2 = Mockito.mock();
        HttpHeaders headers2 = Mockito.mock();
        Repository repository2 = new Repository();
        repository2.setName("bar repo");
        repository2.setUrl("https://bar.com");

        String linkString = "<https://api.github.com/user/583231/repos?per_page=5&page=2>; rel=\"next\", <https://api.github.com/user/583231/repos?per_page=5&page=2>; rel=\"last\"";

        when(githubFeign.getRepositoriesForUsername("foo", 100, 1)).thenReturn(responseEntity1);
        when(responseEntity1.getBody()).thenReturn(List.of(repository1));
        when(responseEntity1.getHeaders()).thenReturn(headers1);
        when(headers1.getOrEmpty(eq("link"))).thenReturn(List.of(linkString));

        when(githubFeign.getRepositoriesForUsername("foo", 100, 2)).thenReturn(responseEntity2);
        when(responseEntity2.getBody()).thenReturn(List.of(repository2));
        when(responseEntity2.getHeaders()).thenReturn(headers2);
        when(headers2.getOrEmpty("link")).thenReturn(List.of());

        List<Repository> returnedRepositories = githubService.getRepositoriesForUsername("foo");

        verify(githubFeign, times(2)).getRepositoriesForUsername(anyString(), anyInt(), anyInt());

        assertEquals(2, returnedRepositories.size());
        assertEquals("foo repo", returnedRepositories.get(0).getName());
        assertEquals("bar repo", returnedRepositories.get(1).getName());
    }

    @Test
    public void testGetRepositoriesRateLimit() {
        long testTime = (System.currentTimeMillis() / 1000L) + 200L;
        when(githubFeign.getRepositoriesForUsername("foo", 100, 1)).thenThrow(new RateLimitException(testTime));

        try {
            githubService.getRepositoriesForUsername("foo");
            fail("Should have returned an exception");
        } catch (TooManyRequestsException tmre) {
            assertThat("Expiration time", tmre.getDelayInSeconds(),greaterThan(0L));
            assertThat("Expiration time", tmre.getDelayInSeconds(), lessThanOrEqualTo(200L));
        }

        verify(githubFeign, times(1)).getRepositoriesForUsername(eq("foo"), eq(100), eq(1));

    }
}
