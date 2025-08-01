package com.seletest.api.utils;

public class ApiConfig {
    
    public static final String BASE_URL = "http://localhost:3001";
    public static final String LOGIN_ENDPOINT = "/login";
    public static final String ITEMS_ENDPOINT = "/items";
    public static final String HEALTH_ENDPOINT = "/health";
    
    // Test credentials
    public static final String VALID_USERNAME_ADMIN = "admin";
    public static final String VALID_PASSWORD_ADMIN = "password";
    public static final String VALID_USERNAME_USER = "user";
    public static final String VALID_PASSWORD_USER = "123456";
    
    public static final String INVALID_USERNAME = "invaliduser";
    public static final String INVALID_PASSWORD = "wrongpassword";
    
    // Content types
    public static final String CONTENT_TYPE_JSON = "application/json";
    
    // Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
}