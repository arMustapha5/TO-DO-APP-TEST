package com.seletest.api.tests;

import com.seletest.api.models.LoginRequest;
import com.seletest.api.utils.ApiConfig;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("Authentication API")
@Feature("User Authentication")
@DisplayName("Authentication API Tests")
public class AuthApiTest extends BaseApiTest {
    
    @Test
    @DisplayName("Valid login with admin credentials")
    @Description("Verify that admin user can login successfully with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User login")
    public void testValidLoginAdmin() {
        LoginRequest loginRequest = new LoginRequest(
            ApiConfig.VALID_USERNAME_ADMIN, 
            ApiConfig.VALID_PASSWORD_ADMIN
        );
        
        given()
            .body(loginRequest)
        .when()
            .post(ApiConfig.LOGIN_ENDPOINT)
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("token", notNullValue())
            .body("token", not(emptyString()))
            .body("user.id", notNullValue())
            .body("user.username", equalTo(ApiConfig.VALID_USERNAME_ADMIN));
    }
    
    @Test
    @DisplayName("Valid login with user credentials")
    @Description("Verify that regular user can login successfully with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User login")
    public void testValidLoginUser() {
        LoginRequest loginRequest = new LoginRequest(
            ApiConfig.VALID_USERNAME_USER, 
            ApiConfig.VALID_PASSWORD_USER
        );
        
        given()
            .body(loginRequest)
        .when()
            .post(ApiConfig.LOGIN_ENDPOINT)
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("token", notNullValue())
            .body("token", not(emptyString()))
            .body("user.id", notNullValue())
            .body("user.username", equalTo(ApiConfig.VALID_USERNAME_USER));
    }
    
    @ParameterizedTest
    @DisplayName("Invalid login attempts")
    @Description("Verify that invalid login attempts are properly rejected")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login validation")
    @CsvSource({
        "admin, wrongpassword, 401",
        "wronguser, password, 401",
        "'', password, 400",
        "admin, '', 400"
    })
    public void testInvalidLogin(String username, String password, int expectedStatus) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        
        given()
            .body(loginRequest)
        .when()
            .post(ApiConfig.LOGIN_ENDPOINT)
        .then()
            .statusCode(expectedStatus)
            .contentType("application/json")
            .body("message", notNullValue())
            .body("message", not(emptyString()));
    }
    
    @Test
    @DisplayName("Login with missing request body")
    @Description("Verify that login fails when request body is missing")
    @Severity(SeverityLevel.NORMAL)
    @Story("Request validation")
    public void testLoginWithMissingBody() {
        given()
        .when()
            .post(ApiConfig.LOGIN_ENDPOINT)
        .then()
            .statusCode(400)
            .contentType("application/json")
            .body("message", notNullValue());
    }
    
    @Test
    @DisplayName("Login with invalid JSON")
    @Description("Verify that login fails with malformed JSON payload")
    @Severity(SeverityLevel.NORMAL)
    @Story("Request validation")
    public void testLoginWithInvalidJson() {
        given()
            .body("invalid json")
        .when()
            .post(ApiConfig.LOGIN_ENDPOINT)
        .then()
            .statusCode(400);
    }
    
    @Test
    @DisplayName("Login with null values")
    @Description("Verify that login fails when username/password are null")
    @Severity(SeverityLevel.NORMAL)
    @Story("Request validation")
    public void testLoginWithNullValues() {
        LoginRequest loginRequest = new LoginRequest(null, null);
        
        given()
            .body(loginRequest)
        .when()
            .post(ApiConfig.LOGIN_ENDPOINT)
        .then()
            .statusCode(400)
            .contentType("application/json")
            .body("message", containsString("required"));
    }
    
    @Test
    @DisplayName("Login endpoint returns correct response structure")
    @Description("Verify that login response has correct structure and doesn't leak password")
    @Severity(SeverityLevel.NORMAL)
    @Story("Response validation")
    public void testLoginResponseStructure() {
        LoginRequest loginRequest = new LoginRequest(
            ApiConfig.VALID_USERNAME_ADMIN, 
            ApiConfig.VALID_PASSWORD_ADMIN
        );
        
        given()
            .body(loginRequest)
        .when()
            .post(ApiConfig.LOGIN_ENDPOINT)
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("$", hasKey("token"))
            .body("$", hasKey("user"))
            .body("user", hasKey("id"))
            .body("user", hasKey("username"))
            .body("user", not(hasKey("password")));  // Ensure password is not returned
    }
}