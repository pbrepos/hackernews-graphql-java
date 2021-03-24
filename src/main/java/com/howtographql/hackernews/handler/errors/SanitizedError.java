package com.howtographql.hackernews.handler.errors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.ExceptionWhileDataFetching;

public class SanitizedError extends ExceptionWhileDataFetching {
    public SanitizedError(ExceptionWhileDataFetching  exception) {
        super(exception.getException());
    }

    @Override
    @JsonIgnore
    public Throwable getException() {
        return super.getException();
    }
}
