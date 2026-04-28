package com.branchapp.gitdata.error;

import lombok.Getter;

@Getter
public class TooManyRequestsException extends RuntimeException {
    final long delayInSeconds;

    public TooManyRequestsException(String message, long delayInSeconds) {
        super(message);
        this.delayInSeconds = delayInSeconds > 0 ? delayInSeconds : 1;
    }
}
