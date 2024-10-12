package com.ketoru.springframework.errors.controllers;

import com.ketoru.springframework.errors.ApiError;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@RestController
public class TestController {

    @GetMapping("bad-request")
    public ResponseEntity<String> errorWithException() {
        throw ApiError.badRequest("This is a test exception");
    }

    @GetMapping("bad-request-by-extension")
    public ResponseEntity<String> errorWithExceptionByExtension() {
        throw ApiError
                .status(HttpStatus.BAD_REQUEST)
                .title("This is a test exception")
                .type("https://example.com/errors/test-exception")
                .instance("https://example.com/errors/test-exception/1")
                .extension("field1", "value1")
                .extension("field2", "value2")
                .detail("This is a test exception");

    }

    @GetMapping("endpoint-with-param")
    public ResponseEntity<String> endpointWithParam(@RequestParam String param) {
        return ResponseEntity.ok("Param: " + param);
    }
}
