package com.hashtag.models;

import java.util.ArrayList;
import java.util.List;

public class Hashtag extends AbstractEntity {
    private String tag;           
    private int usageCount;
    private List<Integer> postIds;

    public Hashtag(String tag) {
        super();
        this.tag = normalizeTag(tag);
        this.usageCount = 0;
        this.postIds = new ArrayList<>();
    }

    public Hashtag(String tag, int initialUsageCount) {
        super();
        this.tag = normalizeTag(tag);
        this.usageCount = initialUsageCount;
        this.postIds = new ArrayList<>();
    }

    private String normalizeTag(String tag) {
        tag = tag.trim().toLowerCase();
        return tag.startsWith("#") ? tag : "#" + tag;
    }

    public void recordUsage(int postId) {
        if (!postIds.contains(postId)) {
            postIds.add(postId);
        }
        usageCount++;
    }

    public String getSummary() {
        return "Hashtag[" + id + "] " + tag + " | Used " + usageCount + " time(s)";
    }

    public String getTag() { return tag; }
    public int getUsageCount() { return usageCount; }
    public List<Integer> getPostIds() { return postIds; }

    public String toString() {
        return getSummary();
    }
}