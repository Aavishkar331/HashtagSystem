package com.hashtag.interfaces;

import java.util.List;

import com.hashtag.entities.Post;
import com.hashtag.entities.User;

public interface Searchable {
    List<Post> search(String hashtag);
    List<Post> search(String... hashtags);
    List<Post> search(String hashtag, String username);
    List<User> searchUsers(String keyword);
}