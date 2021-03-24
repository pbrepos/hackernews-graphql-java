package com.howtographql.hackernews.resolvers;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.howtographql.hackernews.repo.UserRepository;
import com.howtographql.hackernews.model.POJO.Link;
import com.howtographql.hackernews.model.POJO.User;

public class LinkResolver implements GraphQLResolver<Link> {
    private final UserRepository userRepository;

    public LinkResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User postedBy(Link link) {
        if (link.getUserId() == null) {
            return null;
        }

        return userRepository.findById(link.getUserId());
    }
}
