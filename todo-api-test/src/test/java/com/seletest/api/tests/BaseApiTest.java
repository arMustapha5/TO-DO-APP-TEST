package com.seletest.api.tests;

import com.seletest.api.utils.ApiConfig;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public class BaseApiTest {
    
    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = ApiConfig.BASE_URL;
        
        // Enable logging for debugging (can be disabled in production)
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        
        // Set default content type
        RestAssured.requestSpecification = RestAssured.given()
            .contentType(ApiConfig.CONTENT_TYPE_JSON);
    }
    
    @BeforeEach
    public void clearTestData() {
        try {
            given()
            .when()
                .delete("/test/clear-data")
            .then()
                .statusCode(200);
        } catch (Exception e) {
            System.err.println("Warning: Could not clear test data - " + e.getMessage());
        }
    }
}