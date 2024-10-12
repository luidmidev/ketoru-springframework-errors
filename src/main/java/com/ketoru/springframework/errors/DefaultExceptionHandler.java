package com.ketoru.springframework.errors;

import com.ketoru.springframework.errors.config.ErrorsProperties;
import com.ketoru.springframework.errors.config.ErrorsPropertiesAware;
import com.ketoru.springframework.errors.config.ProblemDetailFactoryAware;
import com.ketoru.springframework.errors.schemas.FieldErrorPair;
import jakarta.validation.ConstraintViolationException;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.impl.InvalidContentTypeException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.*;

/**
 * Default Exception Handler
 */
@Log4j2
public abstract class DefaultExceptionHandler extends ResponseEntityExceptionHandler implements ErrorsPropertiesAware, ProblemDetailFactoryAware {

    @Getter
    private ProblemDetailFactory factory;
    private boolean allErrors;

    @Override
    public void setErrorsConfiguration(ErrorsProperties properties) {
        this.allErrors = properties.isAllErrors();
    }

    @Override
    public void setProblemDetailFactory(ProblemDetailFactory factory) {
        this.factory = factory;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleException(Exception ex) {
        return allErrors
                ? factory.createProblemDetailEntity(ex, INTERNAL_SERVER_ERROR, "Internal Server Error", "problemDetail.java.lang.Exception.message", new Object[]{ex.getMessage()})
                : factory.createProblemDetailEntity(ex, INTERNAL_SERVER_ERROR, "Internal Server Error", "problemDetail.java.lang.Exception", null);
    }

    @ExceptionHandler(ApiErrorException.class)
    public ResponseEntity<ProblemDetail> handleApiErrorException(ApiErrorException ex) {
        var problem = ex.getBody();
        return ResponseEntity.status(problem.getStatus()).headers(ex.getHeaders()).body(problem);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolationException(ConstraintViolationException ex) {

        var problem = factory.createProblemDetail(ex, BAD_REQUEST, "One or more fields are invalid");

        factory.addValidationErrors(problem, ex.getConstraintViolations(), violation -> {
            var path = violation.getPropertyPath().toString().split("\\.");
            log.debug("Property path: {}, Class bean {}", violation.getPropertyPath(), violation.getRootBeanClass());
            return new FieldErrorPair(path[path.length - 1], violation.getMessage());
        });

        return ResponseEntity.status(problem.getStatus()).body(problem);
    }

    @ExceptionHandler(InvalidMediaTypeException.class)
    public ResponseEntity<ProblemDetail> handleInvalidMediaTypeException(InvalidMediaTypeException ex) {
        return factory.createProblemDetailEntity(ex, UNSUPPORTED_MEDIA_TYPE, "Invalid Media Type", null, new Object[]{ex.getMediaType()});
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    public ResponseEntity<ProblemDetail> handleInvalidContentTypeException(InvalidContentTypeException ex) {
        return factory.createProblemDetailEntity(ex, UNSUPPORTED_MEDIA_TYPE, "Invalid Content Type");
    }

}
