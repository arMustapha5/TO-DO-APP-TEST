package com.seletest.api.utils;

import com.seletest.api.models.LoginRequest;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthHelper {
    
    public static String getAuthToken(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        
        Response response = given()
            .contentType(ApiConfig.CONTENT_TYPE_JSON)
            .body(loginRequest)
        .when()
            .post(ApiConfig.BASE_URL + ApiConfig.LOGIN_ENDPOINT)
        .then()
            .statusCode(200)
            .extract()
            .response();
        
        return response.jsonPath().getString("token");
    }
    
    public static String getAdminToken() {
        return getAuthToken(ApiConfig.VALID_USERNAME_ADMIN, ApiConfig.VALID_PASSWORD_ADMIN);
    }
    
    public static String getUserToken() {
        return getAuthToken(ApiConfig.VALID_USERNAME_USER, ApiConfig.VALID_PASSWORD_USER);
    }
    
    public static String getBearerToken(String token) {
        return ApiConfig.BEARER_PREFIX + token;
    }
}