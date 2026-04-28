package com.branchapp.gitdata.integration.github.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GithubFeignErrorDecoder implements ErrorDecoder {
    private ErrorDecoder fallback = new Default();

    private static final String RATE_LIMIT_REASON = "rate limit exceeded";
    private static final String RATE_LIMIT_RESET_HEADER = "X-RateLimit-Reset";

    @Override
    public Exception decode(String methodKey, Response response) {
        if (isRateLimit(response)) {
            return new RateLimitException(getRateLimitReset(response));
        }
        return fallback.decode(methodKey, response);
    }

    private boolean isRateLimit(Response response) {
        return response.status() == 403 && RATE_LIMIT_REASON.equals(response.reason());
    }

    /**
     * Parse the response for Github's rate limit reset value.
     *
     * @param response the HTTP response
     * @return parsed rate limit reset value, or 0 if no value could be parsed.
     */
    private long getRateLimitReset(Response response) {
        try {
            return Long.parseLong(response.headers().get(RATE_LIMIT_RESET_HEADER).stream().findFirst().orElse("0"));
        } catch (Exception e) {
            //return 0 on any exception so we don't have to worry about parse errors
            return 0;
        }
    }
}
