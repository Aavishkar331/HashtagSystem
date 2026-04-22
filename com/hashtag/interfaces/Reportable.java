package com.hashtag.interfaces;


import com.hashtag.models.Hashtag;
import com.hashtag.models.Post;
import java.util.List;

public interface Reportable {
    List<Hashtag> getTrending(int topN);

    List<Post> getPostsByHashtag(String hashtag);

    String generateReport(String username);

    String generateReport(String username, int topN);
}
