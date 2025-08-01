package com.seletest.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TodoItem {
    
    @JsonProperty("id")
    private Integer id;
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("completed")
    private Boolean completed;
    
    @JsonProperty("userId")
    private Integer userId;
    
    @JsonProperty("createdAt")
    private String createdAt;
    
    @JsonProperty("updatedAt")
    private String updatedAt;
    
    public TodoItem() {}
    
    public TodoItem(String text) {
        this.text = text;
        this.completed = false;
    }
    
    public TodoItem(String text, Boolean completed) {
        this.text = text;
        this.completed = completed;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Boolean getCompleted() {
        return completed;
    }
    
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}