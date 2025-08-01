package com.seletest.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class LoginPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(css = "[data-testid='username-input']")
    private WebElement usernameInput;
    
    @FindBy(css = "[data-testid='password-input']")
    private WebElement passwordInput;
    
    @FindBy(css = "[data-testid='login-button']")
    private WebElement loginButton;
    
    @FindBy(css = "[data-testid='login-error']")
    private WebElement loginError;
    
    @FindBy(css = "h1")
    private WebElement pageTitle;
    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        PageFactory.initElements(driver, this);
    }
    
    public void navigateToLoginPage(String baseUrl) {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
    }
    
    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }
    
    public void enterPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }
    
    public void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }
    
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }
    
    public void loginAndWaitForSuccess(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
        
        // Wait for successful login by checking for logout button
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='logout-button']")));
    }
    
    public boolean isLoginErrorDisplayed() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.visibilityOf(loginError));
            return loginError.isDisplayed();
        } catch (Exception e) {
            try {
                // Check for common error selectors
                List<WebElement> errorElements = driver.findElements(By.cssSelector(
                    "[data-testid='login-error'], .error, .alert-danger, [role='alert']"
                ));
                
                return errorElements.stream().anyMatch(el -> el.isDisplayed() && !el.getText().trim().isEmpty());
                
            } catch (Exception ex) {
                System.err.println("Exception while searching for error elements: " + ex.getMessage());
            }
            return false;
        }
    }
    
    public String getLoginErrorMessage() {
        try {
            if (loginError.isDisplayed()) {
                return loginError.getText();
            }
        } catch (Exception e) {
            try {
                List<WebElement> errorElements = driver.findElements(By.cssSelector(
                    "[data-testid='login-error'], .error, .alert-danger, [role='alert']"
                ));
                
                return errorElements.stream()
                    .filter(el -> el.isDisplayed() && !el.getText().trim().isEmpty())
                    .map(WebElement::getText)
                    .findFirst()
                    .orElse("");
                    
            } catch (Exception ex) {
                System.err.println("Could not get error message: " + ex.getMessage());
            }
        }
        return "";
    }
    
    public String getPageTitle() {
        wait.until(ExpectedConditions.visibilityOf(pageTitle));
        return pageTitle.getText();
    }
    
    public boolean isLoginPageDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(usernameInput));
            return usernameInput.isDisplayed() && passwordInput.isDisplayed() && loginButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
}