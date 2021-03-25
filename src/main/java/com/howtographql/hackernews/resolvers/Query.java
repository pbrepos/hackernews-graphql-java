package com.howtographql.hackernews.resolvers;

import com.coxautodev.graphql.tools.GraphQLRootResolver;
import com.howtographql.hackernews.model.POJO.LinkFilter;
import com.howtographql.hackernews.repo.LinkRepository;
import com.howtographql.hackernews.model.POJO.Link;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;

import java.util.List;

public class Query {

    private final LinkRepository linkRepository;

    public Query(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @GraphQLQuery
    public List<Link> allLinks(LinkFilter filter,
                               @GraphQLArgument(name = "skip", defaultValue = "0") Number skip, //3
                               @GraphQLArgument(name = "first", defaultValue = "0") Number first) {
        return linkRepository.getAllLinks(filter, skip.intValue(), first.intValue());
    }

    public Link linkByUrl(String id) {
        return linkRepository.findById(id);
    }
}
