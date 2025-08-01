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

public class TodoPage {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    @FindBy(css = "[data-testid='logout-button']")
    private WebElement logoutButton;
    
    @FindBy(css = "[data-testid='new-todo-input']")
    private WebElement newTodoInput;
    
    @FindBy(css = "[data-testid='add-todo-button']")
    private WebElement addTodoButton;
    
    @FindBy(css = "[data-testid='no-todos']")
    private WebElement noTodosMessage;
    
    @FindBy(css = ".user-info span")
    private WebElement welcomeMessage;
    
    @FindBy(css = "h1")
    private WebElement pageTitle;
    
    public TodoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        PageFactory.initElements(driver, this);
    }
    
    public boolean isTodoPageDisplayed() {
        try {
            WebDriverWait extendedWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            extendedWait.until(ExpectedConditions.visibilityOf(logoutButton));
            return logoutButton.isDisplayed() && newTodoInput.isDisplayed() && addTodoButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isTodoPageDisplayedQuick() {
        try {
            WebDriverWait quickWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            quickWait.until(ExpectedConditions.visibilityOf(pageTitle));
            return logoutButton.isDisplayed() && newTodoInput.isDisplayed() && addTodoButton.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public String getWelcomeMessage() {
        wait.until(ExpectedConditions.visibilityOf(welcomeMessage));
        return welcomeMessage.getText();
    }
    
    public void addTodo(String todoText) {
        wait.until(ExpectedConditions.visibilityOf(newTodoInput));
        newTodoInput.clear();
        newTodoInput.sendKeys(todoText);
        
        wait.until(ExpectedConditions.elementToBeClickable(addTodoButton));
        int initialCount = getTodoCount();
        addTodoButton.click();
        
        // Wait for todo count to increase
        wait.until(driver -> getTodoCount() > initialCount);
    }
    
    public boolean isTodoDisplayed(String todoText) {
        try {
            List<WebElement> todoTexts = driver.findElements(By.cssSelector("[data-testid^='todo-text-']"));
            return todoTexts.stream().anyMatch(element -> element.getText().equals(todoText));
        } catch (Exception e) {
            return false;
        }
    }
    
    public void editTodo(int todoId, String newText) {
        WebElement editButton = driver.findElement(By.cssSelector("[data-testid='edit-button-" + todoId + "']"));
        editButton.click();
        
        WebElement editInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='edit-input-" + todoId + "']")));
        editInput.clear();
        editInput.sendKeys(newText);
        
        WebElement saveButton = driver.findElement(By.cssSelector("[data-testid='save-button-" + todoId + "']"));
        saveButton.click();
        
        // Wait for edit mode to close
        wait.until(ExpectedConditions.invisibilityOf(editInput));
    }
    
    public void deleteTodo(int todoId) {
        WebElement deleteButton = driver.findElement(By.cssSelector("[data-testid='delete-button-" + todoId + "']"));
        deleteButton.click();
        
        // Wait for the todo to be removed
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
            By.cssSelector("[data-testid='todo-" + todoId + "']")));
    }
    
    public int getTodoCount() {
        try {
            List<WebElement> todos = driver.findElements(By.cssSelector("[data-testid^='todo-']:not([data-testid*='text']):not([data-testid*='edit']):not([data-testid*='delete']):not([data-testid*='save'])"));
            System.out.println("Current todo count: " + todos.size());
            return todos.size();
        } catch (Exception e) {
            return 0;
        }
    }
    
    public boolean isNoTodosMessageDisplayed() {
        try {
            return noTodosMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }
    
    public int getFirstTodoId() {
        try {
            WebElement firstTodo = driver.findElement(By.cssSelector("[data-testid^='todo-']"));
            String testId = firstTodo.getAttribute("data-testid");
            return Integer.parseInt(testId.replace("todo-", ""));
        } catch (Exception e) {
            return -1;
        }
    }
    
    public String getTodoText(int todoId) {
        try {
            WebElement todoText = driver.findElement(By.cssSelector("[data-testid='todo-text-" + todoId + "']"));
            return todoText.getText();
        } catch (Exception e) {
            return "";
        }
    }
    
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutButton));
        logoutButton.click();
    }
    
    public void clearAllTodos() {
        try {
            // Get all todos at once
            List<WebElement> todos = driver.findElements(By.cssSelector("[data-testid^='todo-']:not([data-testid*='text']):not([data-testid*='edit']):not([data-testid*='delete']):not([data-testid*='save'])"));
            
            if (todos.isEmpty()) {
                return;
            }
            
            // Click all delete buttons first
            for (WebElement todo : todos) {
                try {
                    String testId = todo.getAttribute("data-testid");
                    int todoId = Integer.parseInt(testId.replace("todo-", ""));
                    WebElement deleteButton = driver.findElement(By.cssSelector("[data-testid='delete-button-" + todoId + "']"));
                    deleteButton.click();
                } catch (Exception e) {
                    // Continue if one fails
                }
            }
            
            // Wait for all todos to be gone with shorter timeout
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(driver -> {
                List<WebElement> remainingTodos = driver.findElements(By.cssSelector("[data-testid^='todo-']:not([data-testid*='text']):not([data-testid*='edit']):not([data-testid*='delete']):not([data-testid*='save'])"));
                return remainingTodos.isEmpty();
            });
            
        } catch (Exception e) {
            System.err.println("Could not clear todos via UI: " + e.getMessage());
        }
    }
}