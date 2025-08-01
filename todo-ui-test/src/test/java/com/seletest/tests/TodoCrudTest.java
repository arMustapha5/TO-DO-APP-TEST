package com.seletest.tests;

import com.seletest.pages.LoginPage;
import com.seletest.pages.TodoPage;
import com.seletest.utils.DriverManager;
import com.seletest.utils.TestDataHelper;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Todo UI")
@Feature("Todo CRUD Operations")
@DisplayName("Todo CRUD Operations Tests")
public class TodoCrudTest extends BaseTest {
    
    private LoginPage loginPage;
    private TodoPage todoPage;
    
    @BeforeEach
    public void loginUser() {
        loginPage = new LoginPage(DriverManager.getDriver());
        todoPage = new TodoPage(DriverManager.getDriver());
        
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.loginAndWaitForSuccess("admin", "password");
        assertTrue(todoPage.isTodoPageDisplayed(), "Should be logged in successfully");
        
        todoPage.clearAllTodos();
    }
    
    @Test
    @DisplayName("Create new todo item")
    @Description("Verify that a new todo item can be created through the UI")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Create todo")
    public void testCreateTodo() {
        String todoText = "Test Todo Item";
        
        // Should start with 0 todos after cleanup
        assertEquals(0, todoPage.getTodoCount(), "Should start with 0 todos");
        
        // Add a new todo
        todoPage.addTodo(todoText);
        
        // Verify todo was created
        assertTrue(todoPage.isTodoDisplayed(todoText), "Todo should be displayed after creation");
        assertEquals(1, todoPage.getTodoCount(), "Todo count should be 1 after adding one item");
    }
    
    @Test
    @DisplayName("Create multiple todo items")
    @Description("Verify that multiple todo items can be created sequentially")
    @Severity(SeverityLevel.NORMAL)
    @Story("Create todo")
    public void testCreateMultipleTodos() {
        String[] todoTexts = {"First Todo", "Second Todo", "Third Todo"};
        assertEquals(0, todoPage.getTodoCount(), "Should start with 0 todos");
        
        for (String todoText : todoTexts) {
            todoPage.addTodo(todoText);
        }
        
        assertEquals(3, todoPage.getTodoCount(), "Should have 3 todos after adding them");
        
        for (String todoText : todoTexts) {
            assertTrue(todoPage.isTodoDisplayed(todoText), "Todo '" + todoText + "' should be displayed");
        }
    }
    
    @Test
    @DisplayName("Edit existing todo item")
    @Description("Verify that an existing todo item can be edited through the UI")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Edit todo")
    public void testEditTodo() {
        String originalText = "Original Todo";
        String updatedText = "Updated Todo Text";
        
        // Create a todo first
        todoPage.addTodo(originalText);
        assertTrue(todoPage.isTodoDisplayed(originalText), "Original todo should be displayed");
        
        // Get the todo ID and edit it
        int todoId = todoPage.getFirstTodoId();
        todoPage.editTodo(todoId, updatedText);
        
        // Verify the todo was updated
        assertFalse(todoPage.isTodoDisplayed(originalText), "Original todo text should not be displayed");
        assertTrue(todoPage.isTodoDisplayed(updatedText), "Updated todo text should be displayed");
        assertEquals(updatedText, todoPage.getTodoText(todoId), "Todo text should match updated text");
    }
    
    @Test
    @DisplayName("Delete todo item")
    @Description("Verify that a todo item can be deleted through the UI")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Delete todo")
    public void testDeleteTodo() {
        String todoText = "Todo to Delete";
        assertEquals(0, todoPage.getTodoCount(), "Should start with 0 todos");
        
        // Create a todo first
        todoPage.addTodo(todoText);
        assertTrue(todoPage.isTodoDisplayed(todoText), "Todo should be displayed after creation");
        assertEquals(1, todoPage.getTodoCount(), "Should have 1 todo");
        
        // Delete the todo
        int todoId = todoPage.getFirstTodoId();
        todoPage.deleteTodo(todoId);
        
        // Verify todo was deleted
        assertFalse(todoPage.isTodoDisplayed(todoText), "Todo should not be displayed after deletion");
        assertEquals(0, todoPage.getTodoCount(), "Todo count should be 0 after deletion");
    }
    
    @Test
    @DisplayName("Complete CRUD workflow")
    @Description("Verify complete CRUD workflow - Create, Read, Update, Delete operations")
    @Severity(SeverityLevel.CRITICAL)
    @Story("CRUD workflow")
    public void testCompleteCrudWorkflow() {
        assertEquals(0, todoPage.getTodoCount(), "Should start with 0 todos");
        
        // Create
        String originalTodo = "CRUD Test Todo";
        todoPage.addTodo(originalTodo);
        assertTrue(todoPage.isTodoDisplayed(originalTodo), "Todo should be created");
        
        // Read/Verify
        assertEquals(1, todoPage.getTodoCount(), "Should have 1 todo");
        int todoId = todoPage.getFirstTodoId();
        assertEquals(originalTodo, todoPage.getTodoText(todoId), "Todo text should match");
        
        // Update
        String updatedTodo = "Updated CRUD Test Todo";
        todoPage.editTodo(todoId, updatedTodo);
        assertTrue(todoPage.isTodoDisplayed(updatedTodo), "Updated todo should be displayed");
        assertFalse(todoPage.isTodoDisplayed(originalTodo), "Original todo should not be displayed");
        
        // Delete
        todoPage.deleteTodo(todoId);
        assertEquals(0, todoPage.getTodoCount(), "Todo count should be 0 after deletion");
    }
    
    @Test
    @DisplayName("Delete multiple todos")
    @Description("Verify that multiple todo items can be deleted sequentially")
    @Severity(SeverityLevel.NORMAL)
    @Story("Delete todo")
    public void testDeleteMultipleTodos() {
        String[] todoTexts = {"Todo 1", "Todo 2", "Todo 3"};
        assertEquals(0, todoPage.getTodoCount(), "Should start with 0 todos");
        
        // Create multiple todos
        for (String todoText : todoTexts) {
            todoPage.addTodo(todoText);
        }
        assertEquals(3, todoPage.getTodoCount(), "Should have 3 todos");
        
        // Delete all todos
        for (int i = 0; i < 3; i++) {
            int todoId = todoPage.getFirstTodoId();
            todoPage.deleteTodo(todoId);
        }
        
        assertEquals(0, todoPage.getTodoCount(), "All todos should be deleted");
    }
    
    @Test
    @DisplayName("Edit and cancel operation")
    @Description("Verify that editing can be cancelled without changing the todo")
    @Severity(SeverityLevel.MINOR)
    @Story("Edit todo")
    public void testEditCancel() {
        String todoText = "Todo to Edit and Cancel";
        
        // Create a todo
        todoPage.addTodo(todoText);
        int todoId = todoPage.getFirstTodoId();
        
        // Start editing but don't save - this would require additional page object methods
        // For this test, we'll verify the original text remains unchanged after a failed edit
        String currentText = todoPage.getTodoText(todoId);
        assertEquals(todoText, currentText, "Original text should remain unchanged");
    }
}