package com.hashtag.entities;

public class User extends AbstractEntity{
    
    private String username;
    private String email;
    private String password;
    private boolean loggedIn;

    public User(String username, String email, String password) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.loggedIn = false;
    }

    public User(String username, String email, String password, boolean loggedIn) {
        super();
        this.username = username;
        this.email = email;
        this.password = password;
        this.loggedIn = loggedIn;
    }

    public String getSummary() {
        return "User[" + id + "] " + username + " | " + email;
    }
    
    public boolean verifyPassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public void login(String inputPassword) {
        if (verifyPassword(inputPassword)) {
            this.loggedIn = true;
        }
    }

    public void logout() {
        this.loggedIn = false;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public boolean isLoggedIn() { return loggedIn; }

    public String toString() {
        return getSummary();
    }
}
