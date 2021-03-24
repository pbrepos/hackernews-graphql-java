package com.howtographql.hackernews.resolvers;

import com.coxautodev.graphql.tools.GraphQLRootResolver;
import com.howtographql.hackernews.model.POJO.LinkFilter;
import com.howtographql.hackernews.repo.LinkRepository;
import com.howtographql.hackernews.model.POJO.Link;

import java.util.List;

public class Query implements GraphQLRootResolver {

    private final LinkRepository linkRepository;

    public Query(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public List<Link> allLinks(LinkFilter filter) {
        return linkRepository.getAllLinks(filter);
    }

    public Link linkByUrl(String id) {
        return linkRepository.findById(id);
    }
}
