package com.hashtag.entities;

import java.util.ArrayList;
import java.util.List;

public class Post extends AbstractEntity {

    public static class Metadata {
        private int characterCount;
        private boolean isEdited;

        public Metadata(int characterCount, boolean isEdited) {
            this.characterCount = characterCount;
            this.isEdited = isEdited;
        }

        public int getCharacterCount() { return characterCount; }
        public boolean isEdited() { return isEdited; }
        public void markEdited() { this.isEdited = true; }

        @Override
        public String toString() {
            return "Metadata[chars=" + characterCount + ", edited=" + isEdited + "]";
        }
    }

    private String content;
    private String authorUsername;
    private List<String> hashtags;
    private Metadata metadata;

    public Post(String content, String authorUsername) {
        super();
        this.content = content;
        this.authorUsername = authorUsername;
        this.hashtags = new ArrayList<>();
        this.metadata = new Metadata(content.length(), false);
    }

    public Post(String content, String authorUsername, List<String> hashtags) {
        super();
        this.content = content;
        this.authorUsername = authorUsername;
        this.hashtags = hashtags;
        this.metadata = new Metadata(content.length(), false);
    }
    public void setContent(String newContent) {
        this.content = newContent;
        this.metadata = new Metadata(newContent.length(), true); // marks as edited
    }
    
    public String getSummary() {
        return "Post[" + id + "] by " + authorUsername + ":\n" + content;
    }

    public String getContent() { return content; }
    public String getAuthorUsername() { return authorUsername; }
    public List<String> getHashtags() { return hashtags; }
    public void setHashtags(List<String> hashtags) { this.hashtags = hashtags; }
    public Metadata getMetadata() { return metadata; }

    public String toString() {
        return getSummary();
    }
}
