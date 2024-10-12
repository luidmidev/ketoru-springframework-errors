package com.ketoru.springframework.errors;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_GATEWAY;


@SuppressWarnings("unused")
@Getter
public class ApiError {

    private final HttpStatusCode status;
    private final HttpHeaders headers = new HttpHeaders();
    private String title;
    private URI type;
    private URI instance;
    private Map<String, Object> extensions = new LinkedHashMap<>();

    public ApiError() {
        this(HttpStatus.BAD_REQUEST);
    }

    public ApiError(HttpStatusCode status) {
        this.status = status;
    }

    public static ApiError status(@NotNull HttpStatusCode httpStatusCode) {
        return new ApiError(httpStatusCode);
    }

    public static ApiError status(int httpStatusCode) {
        return status(HttpStatusCode.valueOf(httpStatusCode));
    }

    public ApiError title(String title) {
        this.title = title;
        return this;
    }

    public ApiError type(URI type) {
        this.type = type;
        return this;
    }

    public ApiError type(String type) {
        return type(URI.create(type));
    }

    public ApiError instance(URI instance) {
        this.instance = instance;
        return this;
    }

    public ApiError instance(String instance) {
        return instance(URI.create(instance));
    }

    public ApiError extensions(Map<String, Object> extensions) {
        this.extensions = extensions;
        return this;
    }

    public ApiError extension(String key, Object value) {
        this.extensions.put(key, value);
        return this;
    }

    public ApiError header(String headerName, String... headersValues) {
        this.headers.addAll(headerName, List.of(headersValues));
        return this;
    }

    public ApiError headers(MultiValueMap<String, String> headers) {
        this.headers.addAll(headers);
        return this;
    }


    public ApiErrorException detail(String detail) {
        var problemDetail = getProblemDetail(detail);
        return new ApiErrorException(problemDetail, headers);
    }

    public ProblemDetail getProblemDetail(String detail) {
        var problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        problemDetail.setType(type);
        problemDetail.setInstance(instance);
        if (!extensions.isEmpty()) problemDetail.setProperties(extensions);
        return problemDetail;
    }

    public static ApiErrorException fail(String title, String detail, HttpStatus status) {
        return status(status).title(title).detail(detail);
    }

    private static ApiErrorException fail(String detail, HttpStatus status) {
        return status(status).detail(detail);
    }

    public static ApiErrorException notFound(String title, String detail) {
        return fail(title, detail, NOT_FOUND);
    }

    public static ApiErrorException badRequest(String title, String detail) {
        return fail(title, detail, BAD_REQUEST);
    }

    public static ApiErrorException unauthorized(String title, String detail) {
        return fail(title, detail, UNAUTHORIZED);
    }

    public static ApiErrorException forbidden(String title, String detail) {
        return fail(title, detail, FORBIDDEN);
    }

    public static ApiErrorException conflict(String title, String detail) {
        return fail(title, detail, CONFLICT);
    }

    public static ApiErrorException preconditionFailed(String title, String detail) {
        return fail(title, detail, PRECONDITION_FAILED);
    }

    public static ApiErrorException preconditionRequired(String title, String detail) {
        return fail(title, detail, PRECONDITION_REQUIRED);
    }

    public static ApiErrorException tooManyRequests(String title, String detail) {
        return fail(title, detail, TOO_MANY_REQUESTS);
    }

    public static ApiErrorException internalServerError(String title, String detail) {
        return fail(title, detail, INTERNAL_SERVER_ERROR);
    }

    public static ApiErrorException notImplemented(String title, String detail) {
        return fail(title, detail, NOT_IMPLEMENTED);
    }

    public static ApiErrorException serviceUnavailable(String title, String detail) {
        return fail(title, detail, SERVICE_UNAVAILABLE);
    }

    public static ApiErrorException gatewayTimeout(String title, String detail) {
        return fail(title, detail, GATEWAY_TIMEOUT);
    }

    public static ApiErrorException badGateway(String title, String detail) {
        return fail(title, detail, BAD_GATEWAY);
    }

    public static ApiErrorException notFound(String detail) {
        return fail(detail, NOT_FOUND);
    }

    public static ApiErrorException badRequest(String detail) {
        return fail(detail, BAD_REQUEST);
    }

    public static ApiErrorException unauthorized(String detail) {
        return fail(detail, UNAUTHORIZED);
    }

    public static ApiErrorException forbidden(String detail) {
        return fail(detail, FORBIDDEN);
    }

    public static ApiErrorException conflict(String detail) {
        return fail(detail, CONFLICT);
    }

    public static ApiErrorException preconditionFailed(String detail) {
        return fail(detail, PRECONDITION_FAILED);
    }

    public static ApiErrorException preconditionRequired(String detail) {
        return fail(detail, PRECONDITION_REQUIRED);
    }

    public static ApiErrorException tooManyRequests(String detail) {
        return fail(detail, TOO_MANY_REQUESTS);
    }

    public static ApiErrorException internalServerError(String detail) {
        return fail(detail, INTERNAL_SERVER_ERROR);
    }

    public static ApiErrorException notImplemented(String detail) {
        return fail(detail, NOT_IMPLEMENTED);
    }

    public static ApiErrorException serviceUnavailable(String detail) {
        return fail(detail, SERVICE_UNAVAILABLE);
    }

    public static ApiErrorException gatewayTimeout(String detail) {
        return fail(detail, GATEWAY_TIMEOUT);
    }

    public static ApiErrorException badGateway(String detail) {
        return fail(detail, BAD_GATEWAY);
    }

    public static ApiErrorException notFound() {
        return notFound("Not Found");
    }

    public static ApiErrorException badRequest() {
        return badRequest("Bad Request");
    }

    public static ApiErrorException unauthorized() {
        return unauthorized("Unauthorized");
    }

    public static ApiErrorException forbidden() {
        return forbidden("Forbidden");
    }

    public static ApiErrorException conflict() {
        return conflict("Conflict");
    }

    public static ApiErrorException preconditionFailed() {
        return preconditionFailed("Precondition Failed");
    }

    public static ApiErrorException preconditionRequired() {
        return preconditionRequired("Precondition Required");
    }

    public static ApiErrorException tooManyRequests() {
        return tooManyRequests("Too Many Requests");
    }

    public static ApiErrorException internalServerError() {
        return internalServerError("Internal Server Error");
    }

    public static ApiErrorException notImplemented() {
        return notImplemented("Not Implemented");
    }

    public static ApiErrorException serviceUnavailable() {
        return serviceUnavailable("Service Unavailable");
    }

    public static ApiErrorException gatewayTimeout() {
        return gatewayTimeout("Gateway Timeout");
    }

    public static ApiErrorException badGateway() {
        return badGateway("Bad Gateway");
    }

}