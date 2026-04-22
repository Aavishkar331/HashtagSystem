package com.hashtag.models;

import java.time.LocalDateTime;

public abstract class AbstractEntity {
    private static int idCounter = 0;
    protected int id;
    protected LocalDateTime createdAt;

    public AbstractEntity() {
        this.id = ++idCounter;
        this.createdAt = LocalDateTime.now();
    }

    public abstract String getSummary();

    public int getId() { 
        return id; 
    }
    public LocalDateTime getCreatedAt() {
        return createdAt; 
    }
}
