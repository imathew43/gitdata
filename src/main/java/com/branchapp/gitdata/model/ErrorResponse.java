package com.branchapp.gitdata.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
/**
 * Publicly-readable error response
 */
public class ErrorResponse {
    /**
     * A human-readable message explaining what happened
     */
    private String message;

    /**
     * The class of error encountered
     */
    private Integer code;
}
