package com.hashtag;

import com.hashtag.exceptions.DuplicateUserException;
import com.hashtag.exceptions.InvalidPostException;
import com.hashtag.exceptions.UserNotFoundException;
import com.hashtag.models.Hashtag;
import com.hashtag.models.Post;
import com.hashtag.models.User;
import com.hashtag.services.HashtagTrackingSystem;
import com.hashtag.services.TrendingAnalyzer;
import com.hashtag.services.UserManager;

import java.util.List;
import java.util.Scanner;


/**
 * Entry point of the Hashtag Tracking System.
 * Provides a console-based menu interface.
 */
public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static UserManager userManager = new UserManager();
    private static HashtagTrackingSystem system = new HashtagTrackingSystem(userManager);
    private static TrendingAnalyzer analyzer = new TrendingAnalyzer(system);

    // Wrapper — tracks currently logged in user
    private static String currentUser = null;

    public static void main(String[] args) {
        try {
            userManager.load();
            system.load();
            System.out.println("Data loaded.");
        } catch (Exception e) {
            System.out.println("No saved data found, starting fresh.");
        }
        System.out.println("==============================");
        System.out.println("   Hashtag Tracking System   ");
        System.out.println("==============================");

        boolean running = true;
        while (running) {
            printMenu();
            // Wrapper — Integer for menu choice
            Integer choice = readInt("Enter choice: ");

            switch (choice) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> createPost();
                case 4 -> viewMyPosts();
                case 5 -> editPost();
                case 6 -> deletePost();
                case 7 -> searchByHashtag();
                case 8 -> viewTrending();
                case 9 -> viewReports();
                case 10 -> viewProfile();
                case 11 -> logout();
                case 12 -> {
                    save();
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n------------------------------");
        if (currentUser != null) {
            System.out.println("Logged in as: @" + currentUser);
        }
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Create Post");
        System.out.println("4. View My Posts");
        System.out.println("5. Edit Post");
        System.out.println("6. Delete Post");
        System.out.println("7. Search by Hashtag");
        System.out.println("8. View Trending Hashtags");
        System.out.println("9. View Reports");
        System.out.println("10. View Profile");
        System.out.println("11. Logout");
        System.out.println("12. Save & Exit");
        System.out.println("------------------------------");
    }

    // =====================
    //  FEATURE HANDLERS
    // =====================

    private static void register() {
        System.out.println("\n-- Register --");
        String username = readString("Username: ");
        String email = readString("Email: ");
        String password = readString("Password: ");

        try {
            User user = new User(username, email, password);
            userManager.registerUser(user);
            System.out.println("Registered successfully! Welcome, @" + username);
        } catch (DuplicateUserException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void login() {
        System.out.println("\n-- Login --");
        String username = readString("Username: ");
        String password = readString("Password: ");

        try {
            User user = userManager.loginUser(username, password);
            if (user.isLoggedIn()) {
                currentUser = username;
                System.out.println("Login successful! Welcome back, @" + username);
            } else {
                System.out.println("Incorrect password.");
            }
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void createPost() {
        if (!checkLoggedIn()) return;
        System.out.println("\n-- Create Post --");
        String content = readString("Post content: ");

        try {
            Post post = system.addPost(content, currentUser);
            System.out.println("Post created! Hashtags found: " + post.getHashtags());
        } catch (InvalidPostException | UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewMyPosts() {
        if (!checkLoggedIn()) return;
        System.out.println("\n-- My Posts --");
        List<Post> posts = system.getPostsByUser(currentUser);

        if (posts.isEmpty()) {
            System.out.println("You have no posts yet.");
            return;
        }
        for (Post p : posts) {
            System.out.println(p.getSummary());
            System.out.println("  Hashtags: " + p.getHashtags());
            System.out.println("  " + p.getMetadata());
            System.out.println();
        }
    }
    private static void viewProfile() {
        if (!checkLoggedIn()) return;
        try {
            User user = userManager.findByUsername(currentUser);
            System.out.println("\n-- Profile --");
            System.out.println("Username : " + user.getUsername());
            System.out.println("Email    : " + user.getEmail());
            System.out.println("Status   : " + (user.isLoggedIn() ? "Online" : "Offline"));
            System.out.println("User ID  : " + user.getId());
            System.out.println("Joined   : " + user.getCreatedAt());
        } catch (UserNotFoundException e) { System.out.println("Error: " + e.getMessage()); }
    }
    private static void editPost() {
        if (!checkLoggedIn()) return;
        List<Post> posts = system.getPostsByUser(currentUser);
        if (posts.isEmpty()) { System.out.println("No posts to edit."); return; }
        System.out.println("Your posts:");
        for (Post p : posts) System.out.println("  ID " + p.getId() + ": " + p.getContent());
        Integer postId = readInt("Enter post ID to edit: ");
        try {
            Post post = system.findPostById(postId);
            if (!post.getAuthorUsername().equalsIgnoreCase(currentUser)) {
                System.out.println("You can only edit your own posts."); return;
            }
            String newContent = readString("New content: ");
            system.editPost(postId, newContent);
            System.out.println("Post updated! New hashtags: " + post.getHashtags());
        } catch (InvalidPostException e) { System.out.println("Error: " + e.getMessage()); }
    }

    private static void deletePost() {
        if (!checkLoggedIn()) return;
        List<Post> posts = system.getPostsByUser(currentUser);
        if (posts.isEmpty()) { System.out.println("No posts to delete."); return; }
        System.out.println("Your posts:");
        for (Post p : posts) System.out.println("  ID " + p.getId() + ": " + p.getContent());
        Integer postId = readInt("Enter post ID to delete: ");
        try {
            Post post = system.findPostById(postId);
            if (!post.getAuthorUsername().equalsIgnoreCase(currentUser)) {
                System.out.println("You can only delete your own posts."); return;
            }
            system.deletePost(postId);
            System.out.println("Post deleted successfully.");
        } catch (InvalidPostException e) { System.out.println("Error: " + e.getMessage()); }
    }
    private static void searchByHashtag() {
        System.out.println("\n-- Search by Hashtag --");
        String tag = readString("Enter hashtag (e.g. #food): ");
        List<Post> results = system.search(tag);

        if (results.isEmpty()) {
            System.out.println("No posts found for " + tag);
            return;
        }
        System.out.println("Found " + results.size() + " post(s):");
        for (Post p : results) {
            System.out.println(p.getSummary());
            System.out.println();
        }
    }

    private static void viewTrending() {
        System.out.println("\n-- Trending Hashtags --");
        Integer topN = readInt("Show top how many? ");
        List<Hashtag> trending = analyzer.getTrending(topN);

        if (trending.isEmpty()) {
            System.out.println("No hashtags tracked yet.");
            return;
        }
        System.out.println("\nRank | Hashtag | Uses");
        System.out.println("-----|---------|-----");
        // Wrapper — Integer for rank counter
        Integer rank = 1;
        for (Hashtag h : trending) {
            System.out.println(" " + rank + "   | " + h.getTag()
                             + " | " + h.getUsageCount());
            rank++;
        }
    }

    private static void viewReports() {
        if (!checkLoggedIn()) return;
        System.out.println("1. My report\n2. My report + trending\n3. System report\n4. Most active users");
        Integer choice = readInt("Choice: ");
        switch (choice) {
            case 1 -> System.out.println(analyzer.generateReport(currentUser));
            case 2 -> System.out.println(analyzer.generateReport(currentUser, readInt("Top N: ")));
            case 3 -> System.out.println(analyzer.generateSystemReport());
            case 4 -> {
                Integer topN = readInt("Show top how many users? ");
                List<String> active = analyzer.getMostActiveUsers(topN);
                if (active.isEmpty()) { System.out.println("No users yet."); return; }
                System.out.println("\n-- Most Active Users --");
                Integer rank = 1;
                for (String u : active) { System.out.println(rank + ". " + u); rank++; }
            }
            default -> System.out.println("Invalid.");
        }
    }

    private static void logout() {
        if (!checkLoggedIn()) return;
        try {
            userManager.logoutUser(currentUser);
            System.out.println("Logged out. Goodbye, @" + currentUser);
            currentUser = null;
        } catch (UserNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void save() {
        try {
            userManager.save();
            system.save();
            System.out.println("Data saved.");
        } catch (Exception e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }

    // =====================
    //  HELPER METHODS
    // =====================

    private static boolean checkLoggedIn() {
        if (currentUser == null) {
            System.out.println("Please login first.");
            return false;
        }
        return true;
    }

    private static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static Integer readInt(String prompt) {
        System.out.print(prompt);
        try {
            Integer val = Integer.parseInt(scanner.nextLine().trim());
            return val;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}