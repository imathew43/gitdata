package com.branchapp.gitdata.integration.github.error;

import lombok.Getter;

@Getter
public class RateLimitException extends RuntimeException {
    private final long resetDate;
    public RateLimitException(long resetDate) {
        super("Github rate limit exceeded");
        this.resetDate = resetDate;
    }
}
