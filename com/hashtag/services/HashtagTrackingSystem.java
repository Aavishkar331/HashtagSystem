package com.hashtag.services;

import com.hashtag.exceptions.InvalidPostException;
import com.hashtag.exceptions.UserNotFoundException;
import com.hashtag.interfaces.Searchable;
import com.hashtag.models.Hashtag;
import com.hashtag.models.Post;
import com.hashtag.models.User;
import com.hashtag.utils.FileHandler;
import com.hashtag.utils.HashtagExtractor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HashtagTrackingSystem implements Searchable {

    private List<Post> posts;
    private List<Hashtag> hashtags;
    private UserManager userManager;

    public HashtagTrackingSystem(UserManager userManager) {
        this.posts = new ArrayList<>();
        this.hashtags = new ArrayList<>();
        this.userManager = userManager;
    }

    public Post addPost(String content, String authorUsername)
            throws InvalidPostException, UserNotFoundException {

        if (content == null || content.isBlank()) {
            throw new InvalidPostException("Content cannot be empty");
        }
        if (!userManager.exists(authorUsername)) {
            throw new UserNotFoundException(authorUsername);
        }

        List<String> extractedTags = HashtagExtractor.extract(content);
        Post post = new Post(content, authorUsername, extractedTags);
        posts.add(post);

        for (String tag : extractedTags) {
            recordHashtag(tag, post.getId());
        }

        return post;
    }
    public void editPost(int postId, String newContent) throws InvalidPostException {
        if (newContent == null || newContent.isBlank())
            throw new InvalidPostException("Content cannot be empty");
        Post post = findPostById(postId);
        post.setContent(newContent);
        List<String> newTags = HashtagExtractor.extract(newContent);
        post.setHashtags(newTags);
        for (String tag : newTags) recordHashtag(tag, postId);
    }
    public void deletePost(int postId) throws InvalidPostException {
        Post post = findPostById(postId);
        posts.remove(post);
    }

    private void recordHashtag(String tag, int postId) {
        for (Hashtag h : hashtags) {
            if (h.getTag().equals(tag)) {
                h.recordUsage(postId);
                return;
            }
        }
        // New hashtag
        Hashtag newTag = new Hashtag(tag);
        newTag.recordUsage(postId);
        hashtags.add(newTag);
    }

    public Hashtag findHashtag(String tag) {
        String normalized = tag.toLowerCase().startsWith("#") ? 
                            tag.toLowerCase() : "#" + tag.toLowerCase();
        for (Hashtag h : hashtags) {
            if (h.getTag().equals(normalized)) return h;
        }
        return null;
    }

    public Post findPostById(int id) throws InvalidPostException {
        for (Post p : posts) {
            if (p.getId() == id) return p;
        }
        throw new InvalidPostException("Post with id " + id + " not found");
    }

    public List<Post> getPostsByUser(String username) {
        List<Post> result = new ArrayList<>();
        for (Post p : posts) {
            if (p.getAuthorUsername().equalsIgnoreCase(username)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Post> getAllPosts() { return posts; }
    public List<Hashtag> getAllHashtags() { return hashtags; }

    @Override
    public List<Post> search(String hashtag) {
        List<Post> result = new ArrayList<>();
        String normalized = hashtag.toLowerCase().startsWith("#") ?
                            hashtag.toLowerCase() : "#" + hashtag.toLowerCase();
        for (Post p : posts) {
            if (p.getHashtags().contains(normalized)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Post> search(String... hashtags) {
        List<Post> result = new ArrayList<>();
        for (String tag : hashtags) {
            List<Post> found = search(tag);
            for (Post p : found) {
                if (!result.contains(p)) result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<Post> search(String hashtag, String username) {
        List<Post> result = new ArrayList<>();
        for (Post p : search(hashtag)) {
            if (p.getAuthorUsername().equalsIgnoreCase(username)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userManager.searchUsers(keyword);
    }

    public void save() throws IOException {
        FileHandler.savePosts(posts);
        FileHandler.saveHashtags(hashtags);
    }

    public void load() throws IOException {
        hashtags = FileHandler.loadHashtags();
        posts = FileHandler.loadPosts();
    }
}