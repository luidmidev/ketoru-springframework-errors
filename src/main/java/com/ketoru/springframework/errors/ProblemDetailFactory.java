package com.ketoru.springframework.errors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ketoru.springframework.errors.config.ErrorsProperties;
import com.ketoru.springframework.errors.schemas.FieldErrorPair;
import com.ketoru.springframework.errors.schemas.ValidationError;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Function;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Component
@Log4j2
public class ProblemDetailFactory implements MessageSourceAware {

    private final boolean logErrors;
    private final ObjectMapper mapper;
    private MessageSource messageSource;

    public ProblemDetailFactory(ErrorsProperties properties, ObjectMapper mapper) {
        this.logErrors = properties.isLogErrors();
        this.mapper = mapper;
    }

    @Override
    public void setMessageSource(@NotNull MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    private void dispatchEvents(Exception ex) {
        if (logErrors) log.error("Error: {}", ex.getMessage(), ex);
    }

    public void writeProblemDetail(Exception ex, HttpServletResponse response) throws IOException {
        writeProblemDetail(ex, response, ex instanceof ErrorResponse errorResponse ? errorResponse.getStatusCode() : BAD_REQUEST);
    }

    public void writeProblemDetail(Exception ex, HttpServletResponse response, HttpStatusCode status) throws IOException {
        writeProblemDetail(ex, response, status, null, null, null);
    }

    public void writeProblemDetail(Exception ex, HttpServletResponse response, HttpStatusCode status, @Nullable String detailDetail, @Nullable String detailMessageCode, @Nullable Object[] detailMessageArguments) throws IOException {
        var body = ex instanceof ErrorResponse errorResponse
                ? errorResponse.updateAndGetBody(this.messageSource, LocaleContextHolder.getLocale())
                : createProblemDetail(ex, status, detailDetail, detailMessageCode, detailMessageArguments);

        if (log.isWarnEnabled() && body.getStatus() != status.value()) {
            log.warn("The status code of the error response does not match the status code of the exception. Expected: {}, Actual: {}", status.value(), body.getStatus());
        }

        dispatchEvents(ex);
        writeProblemDetail(body, response);
    }

    public void writeProblemDetail(ProblemDetail body, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setStatus(body.getStatus());
        response.getWriter().write(mapper.writeValueAsString(body));
    }


    public <T> void addValidationErrors(ProblemDetail body, Collection<T> errors, Function<T, FieldErrorPair> mapper) {

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

    public ProblemDetail createProblemDetail(Exception ex, HttpStatusCode status, String defaultDetail) {
        return ErrorResponse
                .builder(ex, status, defaultDetail)
                .build()
                .updateAndGetBody(this.messageSource, LocaleContextHolder.getLocale());

    }

    public ProblemDetail createProblemDetail(Exception ex, HttpStatusCode status, String defaultDetail, @Nullable String detailMessageCode, @Nullable Object[] detailMessageArguments) {
        var builder = ErrorResponse.builder(ex, status, defaultDetail);
        if (detailMessageCode != null) builder.detailMessageCode(detailMessageCode);
        if (detailMessageArguments != null) builder.detailMessageArguments(detailMessageArguments);
        return builder.build().updateAndGetBody(this.messageSource, LocaleContextHolder.getLocale());
    }

    public ResponseEntity<ProblemDetail> createProblemDetailEntity(Exception ex, HttpStatusCode status, String defaultDetail) {
        var problem = createProblemDetail(ex, status, defaultDetail);
        return ResponseEntity.status(problem.getStatus()).body(problem);
    }

    public ResponseEntity<ProblemDetail> createProblemDetailEntity(Exception ex, HttpStatusCode status, String defaultDetail, @Nullable String detailMessageCode, @Nullable Object[] detailMessageArguments) {
        var problem = createProblemDetail(ex, status, defaultDetail, detailMessageCode, detailMessageArguments);
        return ResponseEntity.status(problem.getStatus()).body(problem);
    }


}
