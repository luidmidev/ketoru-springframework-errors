package com.ketoru.springframework.errors.schemas;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationError {

    private final List<Error> errors;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private final List<String> globalErrors;


    public ValidationError() {
        super();
        this.errors = new ArrayList<>();
        this.globalErrors = new ArrayList<>();
    }

    public void addError(String field, String message) {
        var error = errors.stream()
                .filter(e -> e.getField().equals(field))
                .findFirst()
                .orElseGet(() -> Error.merge(field, errors));
        error.getMessages().add(message);
    }

    public void addGlobalError(String message) {
        globalErrors.add(message);
    }

    @Data
    public static class Error {

        private final String field;
        private List<String> messages = new ArrayList<>();

        public static Error merge(String newError, List<Error> errors) {
            var error = new Error(newError);
            errors.add(error);
            return error;
        }
    }
}
