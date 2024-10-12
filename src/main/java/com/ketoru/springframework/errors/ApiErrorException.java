package com.ketoru.springframework.errors;

import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ProblemDetail;

@Getter
public class ApiErrorException extends RuntimeException {

    private final HttpHeaders headers;
    private final ProblemDetail body;

    public ApiErrorException(ProblemDetail body) {
        this(body, new HttpHeaders());
    }

    public ApiErrorException(ProblemDetail body, HttpHeaders headers) {
        this.body = body;
        this.headers = headers;
    }

    public ApiErrorException extension(String field, String value) {
        body.setProperty(field, value);
        return this;
    }

}
