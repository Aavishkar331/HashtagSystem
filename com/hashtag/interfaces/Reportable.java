package com.hashtag.interfaces;


import java.util.List;

import com.hashtag.entities.Hashtag;
import com.hashtag.entities.Post;

public interface Reportable {
    List<Hashtag> getTrending(int topN);

    List<Post> getPostsByHashtag(String hashtag);

    String generateReport(String username);

    String generateReport(String username, int topN);
}
