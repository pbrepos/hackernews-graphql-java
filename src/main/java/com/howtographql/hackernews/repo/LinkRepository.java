package com.howtographql.hackernews.repo;

import com.howtographql.hackernews.resolvers.Link;

import java.util.ArrayList;
import java.util.List;

public class LinkRepository {
    private final List<Link> links;

    public LinkRepository() {
        links = new ArrayList<>();
        links.add(new Link("http://howtographql.com", "Your favorite GraphQL page"));
        links.add(new Link("http://graphql.org/learn/", "The official docks"));
    }

    public List<Link> getAllLinks() {
        return links;
    }

    public Link getLinkBy(String url) {
        Link result = null;
        for (Link link : links) {
            if (link.getUrl().equals(url)) {
                result = link;
                break;
            }
        }

        return result;
    }

    public void saveLink(Link link) {
        links.add(link);
    }
}
