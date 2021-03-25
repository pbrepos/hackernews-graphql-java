package com.howtographql.hackernews;

import com.coxautodev.graphql.tools.SchemaParser;
import com.howtographql.hackernews.handler.errors.SanitizedError;
import com.howtographql.hackernews.repo.LinkRepository;
import com.howtographql.hackernews.repo.UserRepository;
import com.howtographql.hackernews.repo.VoteRepository;
import com.howtographql.hackernews.resolvers.*;
import com.howtographql.hackernews.model.POJO.User;
import com.howtographql.hackernews.resolvers.auth.AuthContext;
import com.howtographql.hackernews.scalars.Scalars;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLContext;
import graphql.servlet.SimpleGraphQLServlet;
import io.leangen.graphql.GraphQLSchemaGenerator;
import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(urlPatterns = "/graphql")
public class GraphQLEndpoint extends SimpleGraphQLServlet {

    private static final LinkRepository LINK_REPOSITORY;
    private static final UserRepository USER_REPOSITORY;
    private static final VoteRepository VOTE_REPOSITORY;


    static {
        //Change to `new MongoClient("<host>:<port>")`
        //if you don't have Mongo running locally on port 27017
        MongoDatabase mongoDatabase = new MongoClient().getDatabase("hackernews");
        LINK_REPOSITORY = new LinkRepository(mongoDatabase.getCollection("links"));
        USER_REPOSITORY = new UserRepository(mongoDatabase.getCollection("users"));
        VOTE_REPOSITORY = new VoteRepository(mongoDatabase.getCollection("votes"));

    }

    public GraphQLEndpoint() {
        super(buildSchema());
    }

//    @NotNull
//    private static GraphQLSchema buildSchema() {
//        return SchemaParser.newParser()
//                .file("schema.graphqls")
//                .resolvers(
//                        new Query(LINK_REPOSITORY),
//                        new Mutation(LINK_REPOSITORY, USER_REPOSITORY, VOTE_REPOSITORY),
//                        new SigninResolver(),
//                        new LinkResolver(USER_REPOSITORY),
//                        new VoteResolver(LINK_REPOSITORY, USER_REPOSITORY))
//                .scalars(Scalars.dateTime)
//                .build()
//                .makeExecutableSchema();
//    }

    private static GraphQLSchema buildSchema() {
        Query query = new Query(LINK_REPOSITORY); //create or inject the service beans
        LinkResolver linkResolver = new LinkResolver(USER_REPOSITORY);
        Mutation mutation = new Mutation(LINK_REPOSITORY, USER_REPOSITORY, VOTE_REPOSITORY);

        return new GraphQLSchemaGenerator()
                .withOperationsFromSingletons(query, linkResolver, mutation) //register the beans
                .generate(); //done :)
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

    @Override
    protected List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
        return errors.stream()
                .filter(err -> err instanceof ExceptionWhileDataFetching || super.isClientError(err))
                .map(err -> err instanceof ExceptionWhileDataFetching ? new SanitizedError((ExceptionWhileDataFetching) err) : err)
                .collect(Collectors.toList());
    }
}
