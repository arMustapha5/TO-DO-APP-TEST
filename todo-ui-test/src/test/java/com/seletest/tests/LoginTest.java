package com.seletest.tests;

import com.seletest.pages.LoginPage;
import com.seletest.pages.TodoPage;
import com.seletest.utils.DriverManager;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Authentication UI")
@Feature("User Login")
@DisplayName("Login Functionality Tests")
public class LoginTest extends BaseTest {
    
    @Test
    @DisplayName("Valid login with admin credentials")
    @Description("Verify that admin user can login successfully through the UI")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User authentication")
    public void testValidLoginAdmin() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        TodoPage todoPage = new TodoPage(DriverManager.getDriver());
        
        loginPage.navigateToLoginPage(BASE_URL);
        assertTrue(loginPage.isLoginPageDisplayed(), "Login page should be displayed");
        assertEquals("Todo App", loginPage.getPageTitle(), "Page title should be 'Todo App'");
        
        loginPage.loginAndWaitForSuccess("admin", "password");
        
        assertTrue(todoPage.isTodoPageDisplayed(), "Todo page should be displayed after successful login");
        assertTrue(todoPage.getWelcomeMessage().contains("admin"), "Welcome message should contain username");
    }
    
    @Test
    @DisplayName("Valid login with user credentials")
    @Description("Verify that regular user can login successfully through the UI")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User authentication")
    public void testValidLoginUser() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        TodoPage todoPage = new TodoPage(DriverManager.getDriver());
        
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.loginAndWaitForSuccess("user", "123456");
        
        assertTrue(todoPage.isTodoPageDisplayed(), "Todo page should be displayed after successful login");
        assertTrue(todoPage.getWelcomeMessage().contains("user"), "Welcome message should contain username");
    }
    
    @ParameterizedTest
    @DisplayName("Invalid login attempts")
    @Description("Verify that invalid login attempts are properly handled in the UI")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Login validation")
    @CsvSource({
        "admin, wrongpassword, Invalid credentials",
        "wronguser, password, Invalid credentials",
        "admin, '', Username and password are required",
        "'', password, Username and password are required"
    })
    public void testInvalidLogin(String username, String password, String expectedErrorType) {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        TodoPage todoPage = new TodoPage(DriverManager.getDriver());
        
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.login(username, password);
        
        // Verify we're still on login page (indicating login failed)
        assertTrue(loginPage.isLoginPageDisplayed(), "Should remain on login page after failed login");
        
        // Verify we're NOT redirected to the todo page
        assertFalse(todoPage.isTodoPageDisplayedQuick(), "Should not be redirected to todo page on invalid login");
        
        // If error message is displayed, check it's not empty
        if (loginPage.isLoginErrorDisplayed()) {
            String errorMessage = loginPage.getLoginErrorMessage();
            assertFalse(errorMessage.isEmpty(), "Error message should not be empty when displayed");
        }
    }
    
    @Test
    @DisplayName("Login page elements are present")
    @Description("Verify that all required login page elements are present and functional")
    @Severity(SeverityLevel.NORMAL)
    @Story("UI elements")
    public void testLoginPageElements() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        
        loginPage.navigateToLoginPage(BASE_URL);
        
        assertTrue(loginPage.isLoginPageDisplayed(), "All login page elements should be present");
        assertEquals("Todo App", loginPage.getPageTitle(), "Page title should be correct");
    }
    
    @Test
    @DisplayName("Logout functionality")
    @Description("Verify that user can logout successfully and return to login page")
    @Severity(SeverityLevel.CRITICAL)
    @Story("User logout")
    public void testLogout() {
        LoginPage loginPage = new LoginPage(DriverManager.getDriver());
        TodoPage todoPage = new TodoPage(DriverManager.getDriver());
        
        // Login first
        loginPage.navigateToLoginPage(BASE_URL);
        loginPage.loginAndWaitForSuccess("admin", "password");
        assertTrue(todoPage.isTodoPageDisplayed(), "Should be on todo page after login");
        
        // Logout
        todoPage.logout();
        
        // Verify we're back to login page
        assertTrue(loginPage.isLoginPageDisplayed(), "Should be back on login page after logout");
    }
}