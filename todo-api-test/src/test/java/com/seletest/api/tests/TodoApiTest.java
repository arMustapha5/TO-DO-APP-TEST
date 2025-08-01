package com.seletest.api.tests;

import com.seletest.api.models.TodoItem;
import com.seletest.api.utils.ApiConfig;
import com.seletest.api.utils.AuthHelper;
import io.qameta.allure.*;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("Todo API")
@Feature("Todo CRUD Operations")
@DisplayName("Todo API Tests")
public class TodoApiTest extends BaseApiTest {
    
    private String authToken;
    
    @BeforeEach
    public void getAuthToken() {
        authToken = AuthHelper.getAdminToken();
    }
    
    @Test
    @DisplayName("Get empty todos list initially")
    @Description("Verify that the todos list is empty when no todos have been created")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Get todos")
    public void testGetEmptyTodosList() {
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
        .when()
            .get(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("", hasSize(0));
    }
    
    @Test
    @DisplayName("Create new todo item")
    @Description("Verify that a new todo item can be created successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create todo")
    public void testCreateTodoItem() {
        TodoItem newTodo = new TodoItem("Test Todo Item");
        
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
            .body(newTodo)
        .when()
            .post(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(201)
            .contentType("application/json")
            .body("id", notNullValue())
            .body("text", equalTo("Test Todo Item"))
            .body("completed", equalTo(false))
            .body("userId", notNullValue())
            .body("createdAt", notNullValue());
    }
    
    @Test
    @DisplayName("Get todos after creating items")
    @Description("Verify that created todos appear in the list when retrieved")
    @Severity(SeverityLevel.NORMAL)
    @Story("Get todos")
    public void testGetTodosAfterCreation() {
        // Create a todo first
        TodoItem newTodo = new TodoItem("Get Todos Test");
        
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
            .body(newTodo)
        .when()
            .post(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(201);
        
        // Now get the todos list
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
        .when()
            .get(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("", hasSize(greaterThan(0)))
            .body("[0].text", equalTo("Get Todos Test"))
            .body("[0].completed", equalTo(false));
    }
    
    @Test
    @DisplayName("Update todo item")
    @Description("Verify that an existing todo item can be updated successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Update todo")
    public void testUpdateTodoItem() {
        // Create a todo first
        TodoItem newTodo = new TodoItem("Original Todo Text");
        
        Integer todoId = given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
            .body(newTodo)
        .when()
            .post(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(201)
            .extract()
            .path("id");
        
        // Update the todo
        TodoItem updatedTodo = new TodoItem("Updated Todo Text");
        
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
            .body(updatedTodo)
        .when()
            .put(ApiConfig.ITEMS_ENDPOINT + "/" + todoId)
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("id", equalTo(todoId))
            .body("text", equalTo("Updated Todo Text"))
            .body("updatedAt", notNullValue());
    }
    
    @Test
    @DisplayName("Delete todo item")
    @Description("Verify that a todo item can be deleted successfully")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete todo")
    public void testDeleteTodoItem() {
        // Create a todo first
        TodoItem newTodo = new TodoItem("Todo to Delete");
        
        Integer todoId = given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
            .body(newTodo)
        .when()
            .post(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(201)
            .extract()
            .path("id");
        
        // Delete the todo
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
        .when()
            .delete(ApiConfig.ITEMS_ENDPOINT + "/" + todoId)
        .then()
            .statusCode(204);
        
        // Verify todo is deleted by trying to get all todos
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
        .when()
            .get(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(200)
            .body("", not(hasItem(hasEntry("id", todoId))));
    }
    
    @Test
    @DisplayName("Unauthorized access without token")
    @Description("Verify that API endpoints are protected and require authentication")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Authentication")
    public void testUnauthorizedAccess() {
        // Try to get todos without authorization header
        given()
        .when()
            .get(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(401)
            .body("message", containsString("Access token required"));
        
        // Try to create todo without authorization header
        TodoItem newTodo = new TodoItem("Unauthorized Todo");
        
        given()
            .body(newTodo)
        .when()
            .post(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(401)
            .body("message", containsString("Access token required"));
    }
    
    @Test
    @DisplayName("Invalid token access")
    @Description("Verify that invalid tokens are rejected")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Authentication")
    public void testInvalidTokenAccess() {
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, "Bearer invalid_token")
        .when()
            .get(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(403)
            .body("message", containsString("Invalid or expired token"));
    }
    
    @ParameterizedTest
    @DisplayName("Create todo with invalid data")
    @Description("Verify that todo creation fails with invalid text data")
    @Severity(SeverityLevel.NORMAL)
    @Story("Data validation")
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    public void testCreateTodoWithInvalidText(String invalidText) {
        TodoItem invalidTodo = new TodoItem(invalidText);
        
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
            .body(invalidTodo)
        .when()
            .post(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(400)
            .body("message", containsString("Todo text is required"));
    }
    
    @Test
    @DisplayName("Update non-existent todo")
    @Description("Verify that updating a non-existent todo returns 404 error")
    @Severity(SeverityLevel.NORMAL)
    @Story("Error handling")
    public void testUpdateNonExistentTodo() {
        TodoItem updatedTodo = new TodoItem("Updated Text");
        
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
            .body(updatedTodo)
        .when()
            .put(ApiConfig.ITEMS_ENDPOINT + "/99999")
        .then()
            .statusCode(404)
            .body("message", containsString("Todo not found"));
    }
    
    @Test
    @DisplayName("Delete non-existent todo")
    @Description("Verify that deleting a non-existent todo returns 404 error")
    @Severity(SeverityLevel.NORMAL)
    @Story("Error handling")
    public void testDeleteNonExistentTodo() {
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
        .when()
            .delete(ApiConfig.ITEMS_ENDPOINT + "/99999")
        .then()
            .statusCode(404)
            .body("message", containsString("Todo not found"));
    }
    
    @Test
    @DisplayName("User isolation - users can only access their own todos")
    @Description("Verify that users can only access their own todos and not others")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User isolation")
    public void testUserIsolation() {
        // Create todo with admin user
        TodoItem adminTodo = new TodoItem("Admin Todo");
        
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(authToken))
            .body(adminTodo)
        .when()
            .post(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(201);
        
        // Get todos with different user token
        String userToken = AuthHelper.getUserToken();
        
        given()
            .header(ApiConfig.AUTHORIZATION_HEADER, AuthHelper.getBearerToken(userToken))
        .when()
            .get(ApiConfig.ITEMS_ENDPOINT)
        .then()
            .statusCode(200)
            .body("", hasSize(0));  // User should not see admin's todos
    }
}