package com.hashtag.services;

import com.hashtag.entities.Hashtag;
import com.hashtag.entities.Post;
import com.hashtag.interfaces.Reportable;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes hashtag usage and generates reports.
 * Implements Reportable interface.
 */
public class TrendingAnalyzer implements Reportable {

    private HashtagTrackingSystem system;

    public TrendingAnalyzer(HashtagTrackingSystem system) {
        this.system = system;
    }

    // Returns top N hashtags sorted by usage count (descending)
    @Override
    public List<Hashtag> getTrending(int topN) {
        List<Hashtag> all = new ArrayList<>(system.getAllHashtags());

        // Bubble sort by usageCount descending
        for (int i = 0; i < all.size() - 1; i++) {
            for (int j = 0; j < all.size() - i - 1; j++) {
                if (all.get(j).getUsageCount() < all.get(j + 1).getUsageCount()) {
                    Hashtag temp = all.get(j);
                    all.set(j, all.get(j + 1));
                    all.set(j + 1, temp);
                }
            }
        }

        // Return only top N
        int limit = Math.min(topN, all.size());
        return all.subList(0, limit);
    }

    @Override
    public List<Post> getPostsByHashtag(String hashtag) {
        return system.search(hashtag);
    }

    // Report for a user — shows their posts and hashtags used
    @Override
    public String generateReport(String username) {
        List<Post> userPosts = system.getPostsByUser(username);
        StringBuilder sb = new StringBuilder();

        sb.append("=== Report for @").append(username).append(" ===\n");
        sb.append("Total posts: ").append(userPosts.size()).append("\n");

        // Wrapper usage — Integer to count unique hashtags
        Integer uniqueTagCount = 0;
        List<String> seen = new ArrayList<>();
        for (Post p : userPosts) {
            for (String tag : p.getHashtags()) {
                if (!seen.contains(tag)) {
                    seen.add(tag);
                    uniqueTagCount++;
                }
            }
        }

        sb.append("Unique hashtags used: ").append(uniqueTagCount).append("\n");
        sb.append("Posts:\n");
        for (Post p : userPosts) {
            sb.append("  - ").append(p.getSummary()).append("\n");
        }
        return sb.toString();
    }

    // Report with trending hashtags limit
    @Override
    public String generateReport(String username, int topN) {
        String base = generateReport(username);
        StringBuilder sb = new StringBuilder(base);

        sb.append("\nTop ").append(topN).append(" trending hashtags:\n");
        List<Hashtag> trending = getTrending(topN);
        for (Hashtag h : trending) {
            sb.append("  ").append(h.getTag())
              .append(" — ").append(h.getUsageCount()).append(" uses\n");
        }
        return sb.toString();
    }
    public List<String> getMostActiveUsers(int topN) {
        List<String> usernames = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        for (Post p : system.getAllPosts()) {
            String author = p.getAuthorUsername();
            int index = usernames.indexOf(author);
            if (index == -1) {
                usernames.add(author);
                counts.add(1);
            } else {
                counts.set(index, counts.get(index) + 1);
            }
        }

        // bubble sort descending by post count
        for (int i = 0; i < usernames.size() - 1; i++) {
            for (int j = 0; j < usernames.size() - i - 1; j++) {
                if (counts.get(j) < counts.get(j + 1)) {
                    String tempUser = usernames.get(j);
                    usernames.set(j, usernames.get(j + 1));
                    usernames.set(j + 1, tempUser);
                    Integer tempCount = counts.get(j);
                    counts.set(j, counts.get(j + 1));
                    counts.set(j + 1, tempCount);
                }
            }
        }

        List<String> result = new ArrayList<>();
        for (int i = 0; i < Math.min(topN, usernames.size()); i++) {
            result.add(usernames.get(i) + " (" + counts.get(i) + " posts)");
        }
        return result;
    }
    // Summary report of entire system
    public String generateSystemReport() {
        Integer totalPosts = system.getAllPosts().size();
        Integer totalHashtags = system.getAllHashtags().size();
        Boolean hasData = totalPosts > 0;
        StringBuilder sb = new StringBuilder();
        sb.append("=== System Report ===\n");
        sb.append("Total posts    : ").append(totalPosts).append("\n");
        sb.append("Unique hashtags: ").append(totalHashtags).append("\n");
        sb.append("Has data       : ").append(hasData).append("\n");

        sb.append("\nMost Active Users:\n");
        List<String> activeUsers = getMostActiveUsers(5);
        if (activeUsers.isEmpty()) sb.append("  No users yet.\n");
        else for (String u : activeUsers) sb.append("  ").append(u).append("\n");

        sb.append("\nTop 5 Trending:\n");
        for (Hashtag h : getTrending(5))
            sb.append("  ").append(h.getTag()).append(" - ").append(h.getUsageCount()).append(" uses\n");
        return sb.toString();
    }
}