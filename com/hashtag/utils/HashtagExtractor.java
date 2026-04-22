package com.hashtag.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HashtagExtractor {

    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#\\w+");

    public static List<String> extract(String content) {
        List<String> hashtags = new ArrayList<>();
        if (content == null || content.isBlank()) return hashtags;

        Matcher matcher = HASHTAG_PATTERN.matcher(content.toLowerCase());
        while (matcher.find()) {
            String tag = matcher.group();
            if (!hashtags.contains(tag)) {
                hashtags.add(tag);
            }
        }
        return hashtags;
    }

    public static List<String> extract(String... contents) {
        List<String> allTags = new ArrayList<>();
        for (String content : contents) {
            List<String> tags = extract(content);
            for (String tag : tags) {
                if (!allTags.contains(tag)) {
                    allTags.add(tag);
                }
            }
        }
        return allTags;
    }

    public static List<String> extract(String content, int minLength) {
        List<String> hashtags = extract(content);
        List<String> filtered = new ArrayList<>();
        for (String tag : hashtags) {
            if (tag.length() - 1 >= minLength) {
                filtered.add(tag);
            }
        }
        return filtered;
    }
}