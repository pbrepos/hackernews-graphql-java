package com.howtographql.hackernews;

import com.coxautodev.graphql.tools.SchemaParser;
import com.howtographql.hackernews.repo.LinkRepository;
import com.howtographql.hackernews.repo.UserRepository;
import com.howtographql.hackernews.resolvers.LinkResolver;
import com.howtographql.hackernews.resolvers.Mutation;
import com.howtographql.hackernews.resolvers.POJO.User;
import com.howtographql.hackernews.resolvers.Query;
import com.howtographql.hackernews.resolvers.SigninResolver;
import com.howtographql.hackernews.resolvers.auth.AuthContext;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;
import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

    private static final LinkRepository LINK_REPOSITORY;
    private static final UserRepository USER_REPOSITORY;


    static {
        //Change to `new MongoClient("<host>:<port>")`
        //if you don't have Mongo running locally on port 27017
        MongoDatabase mongoDatabase = new MongoClient().getDatabase("hackernews");
        LINK_REPOSITORY = new LinkRepository(mongoDatabase.getCollection("links"));
        USER_REPOSITORY = new UserRepository(mongoDatabase.getCollection("users"));

    }

    public GraphQLEndpoint() {
        super(buildSchema());
    }

    @NotNull
    private static GraphQLSchema buildSchema() {
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(
                        new Query(LINK_REPOSITORY),
                        new Mutation(LINK_REPOSITORY, USER_REPOSITORY),
                        new SigninResolver(),
                        new LinkResolver(USER_REPOSITORY))
                .build()
                .makeExecutableSchema();
    }

    @Override
    protected GraphQLContext createContext(Optional<HttpServletRequest> request, Optional<HttpServletResponse> response) {
        User user = request
                .map(req -> req.getHeader("Authorization"))
                .filter(id -> !id.isEmpty())
                .map(id -> id.replace("Bearer ", ""))
                .map(USER_REPOSITORY::findById)
                .orElse(null);

        return new AuthContext(user, request, response);
    }
}
