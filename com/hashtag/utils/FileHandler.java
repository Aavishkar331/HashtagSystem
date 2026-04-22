package com.hashtag.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.hashtag.entities.Hashtag;
import com.hashtag.entities.Post;
import com.hashtag.entities.User;

public class FileHandler {

    private static final String USERS_FILE = "data/users.txt";
    private static final String POSTS_FILE = "data/posts.txt";
    private static final String HASHTAGS_FILE = "data/hashtags.txt";

    public static void saveUsers(List<User> users) throws IOException {
        new File("data").mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                writer.write(u.getUsername() + "," + u.getEmail() + "," + u.isLoggedIn());
                writer.newLine();
            }
        }
    }

    public static void savePosts(List<Post> posts) throws IOException {
        new File("data").mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(POSTS_FILE))) {
            for (Post p : posts) {
                writer.write(p.getId() + "," + p.getAuthorUsername() + "," + p.getContent());
                writer.newLine();
            }
        }
    }

    public static void saveHashtags(List<Hashtag> hashtags) throws IOException {
        new File("data").mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HASHTAGS_FILE))) {
            for (Hashtag h : hashtags) {
                writer.write(h.getTag() + "," + h.getUsageCount());
                writer.newLine();
            }
        }
    }

    public static List<String> loadRawLines(String filepath) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(filepath);
        if (!file.exists()) return lines;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) lines.add(line.trim());
            }
        }
        return lines;
    }

    public static List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        for (String line : loadRawLines(USERS_FILE)) {
            String[] parts = line.split(",", 3);
            if (parts.length < 2) continue;
            users.add(new User(parts[0], parts[1], "N/A"));
        }
        return users;
    }

    public static List<Hashtag> loadHashtags() throws IOException {
        List<Hashtag> hashtags = new ArrayList<>();
        for (String line : loadRawLines(HASHTAGS_FILE)) {
            String[] parts = line.split(",", 2);
            if (parts.length < 2) continue;
            hashtags.add(new Hashtag(parts[0], Integer.parseInt(parts[1])));
        }
        return hashtags;
    }
    public static List<Post> loadPosts() throws IOException {
        List<Post> posts = new ArrayList<>();
        for (String line : loadRawLines(POSTS_FILE)) {
            String[] parts = line.split(",", 3);
            if (parts.length < 3) continue;
            List<String> tags = com.hashtag.utils.HashtagExtractor.extract(parts[2]);
            posts.add(new Post(parts[2], parts[1], tags));
        }
        return posts;
    }
}