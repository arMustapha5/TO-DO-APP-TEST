package com.seletest.utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class TestDataHelper {
    
    private static final String BASE_URL = "http://localhost:3001";
    private static boolean apiAvailable = true;
    
    public static void clearTestData() {
        if (!apiAvailable) {
            System.out.println("INFO: Using UI-based test data cleanup since API is not available.");
            return;
        }
        
        for (int attempt = 0; attempt < 2; attempt++) {
            try {
                Response response = given()
                    .baseUri(BASE_URL)
                .when()
                    .delete("/api/todos");
                
                if (response.getStatusCode() == 200) {
                    return;
                }
            } catch (Exception e) {
                // Try fallback endpoint
            }
            
            try {
                Response response = given()
                    .baseUri(BASE_URL)
                .when()
                    .delete("/test/clear-data");
                
                if (response.getStatusCode() == 200) {
                    return;
                }
            } catch (Exception e) {
                if (attempt == 0) {
                    System.out.println("INFO: API endpoints not available for data clearing. Will rely on UI-based cleanup.");
                }
            }
        }
        
        apiAvailable = false;
        System.out.println("INFO: Using UI-based test data cleanup since API is not available.");
    }
}