# GitData

## Overview

Service to retrieve user and repository data from github.

## Running the Code

```shell
./gradlew bootRun
```

## Testing

Test coverage should be as comprehensive as is practical. Unit test coverage exists for all nontrivial services.
Integration tests exist for all Controllers, and should cover every documented status code.

### Running Tests

```shell
./gradlew test
```

## Architecture

* SpringBoot 3.5
* Java 21

### Configuration

Currently set to listen on Port 8080, supporting HTTP - can be configured differently.

### Caching

Caching utilizing default Spring Caching and `@Cacheable` interface with permanent TTL.

TODO: switch to ehcache or redis and configure TTL.

### Github APIs

Makes use of Github core APIs for retrieving user and repositories for user.  See: [Github Documentation](https://docs.github.com/en/rest)
