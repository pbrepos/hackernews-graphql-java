package com.howtographql.hackernews.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Scalars {

    public static GraphQLScalarType dateTime = new GraphQLScalarType("DateTime", "DateTime scalar", new Coercing() {
        @Override
        public Object serialize(Object o) {
            //serialize the ZonedDateTime into string on the way out
            return ((ZonedDateTime)o).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }

        @Override
        public Object parseValue(Object o) {
            return serialize(o);
        }

        @Override
        public Object parseLiteral(Object o) {
            if (o instanceof StringValue) {
                return ZonedDateTime.parse(((StringValue) o).getValue());
            } else {
                return null;
            }
        }
    });
}
