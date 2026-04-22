package com.hashtag.interfaces;

import com.hashtag.models.Post;
import com.hashtag.models.User;
import java.util.List;

public interface Searchable {
    List<Post> search(String hashtag);
    List<Post> search(String... hashtags);
    List<Post> search(String hashtag, String username);
    List<User> searchUsers(String keyword);
}