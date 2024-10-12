package com.ketoru.springframework.errors.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class TestControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {
    }

    @Test
    void errorWithException() throws Exception {

        var result = mockMvc.perform(get("/bad-request")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("This is a test exception")))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Response: " + jsonResponse);
    }

    @Test
    void errorWithExceptionByExtension() throws Exception {

        var result = mockMvc.perform(get("/bad-request-by-extension")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("This is a test exception")))
                .andExpect(content().string(containsString("field1")))
                .andExpect(content().string(containsString("value1")))
                .andExpect(content().string(containsString("field2")))
                .andExpect(content().string(containsString("value2")))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Response: " + jsonResponse);
    }

    @Test
    void endpointWithParam() throws Exception {

        var result = mockMvc.perform(get("/endpoint-with-param")
                        .param("param", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Param: test")))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Response: " + jsonResponse);
    }

    @Test
    void endpointWithParamError() throws Exception {

        var result = mockMvc.perform(get("/endpoint-with-param")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("The required request parameter 'param' is missing")))
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        System.out.println("Response: " + jsonResponse);
    }

}