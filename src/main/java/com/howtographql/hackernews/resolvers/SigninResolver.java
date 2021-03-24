package com.howtographql.hackernews.resolvers;

import com.coxautodev.graphql.tools.GraphQLRootResolver;
import com.howtographql.hackernews.model.POJO.User;

public class SigninResolver implements GraphQLRootResolver {
    public User user(SigninPayload payload) {
        return payload.getUser();
    }
}
