package com.hashtag.services;

import com.hashtag.exceptions.DuplicateUserException;
import com.hashtag.exceptions.UserNotFoundException;
import com.hashtag.interfaces.Searchable;
import com.hashtag.models.Post;
import com.hashtag.models.User;
import com.hashtag.utils.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManager implements Searchable {

    private List<User> users;

    public UserManager() {
        this.users = new ArrayList<>();
    }

    public void registerUser(User user) throws DuplicateUserException {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(user.getUsername())) {
                throw new DuplicateUserException(user.getUsername());
            }
        }
        users.add(user);
    }

    public User loginUser(String username, String password) throws UserNotFoundException {
        User user = findByUsername(username);
        user.login(password);
        return user;
    }

    public void logoutUser(String username) throws UserNotFoundException {
        User user = findByUsername(username);
        user.logout();
    }

    public void deleteUser(String username) throws UserNotFoundException {
        User user = findByUsername(username);
        users.remove(user);
    }

    public User findByUsername(String username) throws UserNotFoundException {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        throw new UserNotFoundException(username);
    }

    public boolean exists(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public List<Post> search(String hashtag) {
        return new ArrayList<>();
    }

    @Override
    public List<Post> search(String... hashtags) {
        return new ArrayList<>();
    }

    @Override
    public List<Post> search(String hashtag, String username) {
        return new ArrayList<>();
    }

    @Override
    public List<User> searchUsers(String keyword) {
        List<User> results = new ArrayList<>();
        String lower = keyword.toLowerCase();
        for (User u : users) {
            if (u.getUsername().toLowerCase().contains(lower) ||
                u.getEmail().toLowerCase().contains(lower)) {
                results.add(u);
            }
        }
        return results;
    }

    public void save() throws IOException {
        FileHandler.saveUsers(users);
    }

    public void load() throws IOException {
        users = FileHandler.loadUsers();
    }
}