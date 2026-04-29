# GitData

## Overview

Service to retrieve user and repository data from github.

## Running the Code

### Requirements

* Java 21

### Run

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

Data from Github is cached utilizing default Spring Caching and `@Cacheable` interface.

Repository cache is cleared daily at midnight, as repository information may change often.

User cache is cleared weekly, Sunday at Midnight, as User information is unlikely to change often.


TODO: switch to ehcache or redis and configure TTL.

### Github APIs

Makes use of Github core APIs for retrieving user and repositories for user.  See: [Github Documentation](https://docs.github.com/en/rest)
