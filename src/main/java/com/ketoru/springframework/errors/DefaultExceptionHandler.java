package com.ketoru.springframework.errors;

import com.ketoru.springframework.errors.config.ErrorsProperties;
import com.ketoru.springframework.errors.config.ErrorsPropertiesAware;
import com.ketoru.springframework.errors.schemas.FieldErrorPair;
import com.ketoru.springframework.errors.schemas.ValidationError;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.*;

/**
 * Default Exception Handler
 */
@Log4j2
public abstract class DefaultExceptionHandler extends ResponseEntityExceptionHandler implements ErrorsPropertiesAware {

    private boolean allErrors;
    private boolean logErrors;
    private boolean sendStackTrace;

    @Override
    public void setErrorsConfiguration(ErrorsProperties properties) {
        this.allErrors = properties.isAllErrors();
        this.logErrors = properties.isLogErrors();
        this.sendStackTrace = properties.isSendStackTrace();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefaultException(Exception ex, WebRequest request) {
        return allErrors
                ? createDefaultResponseEntity(ex, new HttpHeaders(), INTERNAL_SERVER_ERROR, ex.getMessage(), "problemDetail.java.lang.Exception.message", new Object[]{ex.getMessage()}, request)
                : createDefaultResponseEntity(ex, new HttpHeaders(), INTERNAL_SERVER_ERROR, "Internal Server Error", "problemDetail.java.lang.Exception", null, request);
    }

    @ExceptionHandler(ApiErrorException.class)
    public ResponseEntity<ProblemDetail> handleApiErrorException(ApiErrorException ex) {
        var problem = ex.getBody();
        return ResponseEntity.status(problem.getStatus()).headers(ex.getHeaders()).body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        var defaultDetail = "One or more fields are invalid.";
        var body = createProblemDetail(ex, BAD_REQUEST, defaultDetail, null, null, request);

        addValidationErrors(body, ex.getConstraintViolations(), violation -> {
            var path = violation.getPropertyPath().toString().split("\\.");
            log.debug("Property path: {}, Class bean {}", violation.getPropertyPath(), violation.getRootBeanClass());
            return new FieldErrorPair(path[path.length - 1], violation.getMessage());
        });

        return createResponseEntity(body, new HttpHeaders(), BAD_REQUEST, request);
    }

    @ExceptionHandler(InvalidMediaTypeException.class)
    public ResponseEntity<Object> handleInvalidMediaTypeException(InvalidMediaTypeException ex, WebRequest request) {
        var defaultDetail = "The media type '" + ex.getMediaType() + "' is not supported.";
        var detailMessageArguments = new Object[]{ex.getMediaType()};
        return createDefaultResponseEntity(ex, new HttpHeaders(), BAD_REQUEST, defaultDetail, null, detailMessageArguments, request);
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    public ResponseEntity<Object> handleInvalidContentTypeException(InvalidContentTypeException ex, WebRequest request) {
        var defaultDetail = "Invalid Content Type.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), BAD_REQUEST, defaultDetail, null, null, request);
    }

    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<Object> handleAccountExpiredException(AccountExpiredException ex, WebRequest request) {
        var defaultDetail = "User account has expired.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, null, request);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<Object> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex, WebRequest request) {
        var defaultDetail = "Authentication credentials were not found.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, null, request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        var defaultDetail = "Invalid username or password.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, null, request);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<Object> handleCredentialsExpiredException(CredentialsExpiredException ex, WebRequest request) {
        var defaultDetail = "User credentials have expired.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, null, request);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<Object> handleDisabledException(DisabledException ex, WebRequest request) {
        var defaultDetail = "User account is disabled.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, null, request);
    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<Object> handleInsufficientAuthenticationException(InsufficientAuthenticationException ex, WebRequest request) {
        var defaultDetail = "Insufficient authentication provided.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, null, request);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<Object> handleLockedException(LockedException ex, WebRequest request) {
        var defaultDetail = "User account is locked.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, null, request);
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity<Object> handleProviderNotFoundException(ProviderNotFoundException ex, WebRequest request) {
        var defaultDetail = "Authentication provider not found.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, null, request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        var defaultDetail = "User not found.";
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, null, request);
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    public ResponseEntity<Object> handleAuthenticationServiceException(AuthenticationServiceException ex, WebRequest request) {
        var defaultDetail = "An error occurred during authentication, detail: " + ex.getMessage();
        var detailMessageArguments = new Object[]{ex.getMessage()};
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, detailMessageArguments, request);
    }

    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<Object> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException ex, WebRequest request) {
        var defaultDetail = "An internal error occurred during authentication, detail: " + ex.getMessage();
        var detailMessageArguments = new Object[]{ex.getMessage()};
        return createDefaultResponseEntity(ex, new HttpHeaders(), UNAUTHORIZED, defaultDetail, null, detailMessageArguments, request);
    }

    protected ResponseEntity<Object> createDefaultResponseEntity(Exception ex, HttpHeaders headers, HttpStatusCode statusCode, String defaultDetail, @Nullable String detailMessageCode, Object[] detailMessageArguments, WebRequest request) {
        var body = createProblemDetail(ex, statusCode, defaultDetail, detailMessageCode, detailMessageArguments, request);
        return createResponseEntity(body, headers, statusCode, request);
    }

    protected static <T> void addValidationErrors(ProblemDetail body, Collection<T> errors, Function<T, FieldErrorPair> mapper) {
        var validations = new ValidationError();
        for (var error : errors) {
            var fieldError = mapper.apply(error);
            if (fieldError.field() != null) validations.addError(fieldError.field(), fieldError.message());
            else validations.addGlobalError(fieldError.message());
        }

        var errorsMap = validations.getErrors();
        var globalErrors = validations.getGlobalErrors();

        if (!errorsMap.isEmpty()) body.setProperty("errors", errorsMap);
        if (!globalErrors.isEmpty()) body.setProperty("globalErrors", globalErrors);
    }

    private void dispatchEvents(Exception ex, Object body) {
        if (logErrors) log.error("Error: {}", ex.getMessage(), ex);
        if (sendStackTrace && body instanceof ProblemDetail problemDetail) problemDetail.setProperty("stackTrace", getStackTrace(ex));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(@NotNull Exception ex, @Nullable Object body, @NotNull HttpHeaders headers, @NotNull HttpStatusCode statusCode, @NotNull WebRequest request) {
        var response = super.handleExceptionInternal(ex, body, headers, statusCode, request);
        dispatchEvents(ex, response != null ? response.getBody() : body);
        return response;
    }

    static String getStackTrace(Throwable throwable) {
        if (throwable == null) return "";
        var sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
