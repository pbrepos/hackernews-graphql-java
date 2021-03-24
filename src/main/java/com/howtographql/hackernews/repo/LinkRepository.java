package com.howtographql.hackernews.repo;

import com.howtographql.hackernews.model.POJO.Link;
import com.howtographql.hackernews.model.POJO.LinkFilter;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.*;

public class LinkRepository {
    private final MongoCollection<Document> links;

    public LinkRepository(MongoCollection<Document> links) {
        this.links = links;
    }

    public List<Link> getAllLinks(LinkFilter filter) {
        Optional<Bson> mongoFilter = Optional.ofNullable(filter).map(this::buildFilter);
        List<Link> allLinks = new ArrayList<>();
        for (Document doc : mongoFilter.map(links::find).orElseGet(links::find)) {
            allLinks.add(link(doc));
        }
        return allLinks;
    }

    public Link findById(String id) {
        Document doc = links.find(eq("_id", new ObjectId(id))).first();
        return link(doc);
    }

    public Link getLinkBy(String url) {
        Link result = null;
        for (Document doc: links.find()) {
            if (doc.getString("url").equals(url)) {
                result = link(doc);
                break;
            }
        }

        return result;
    }


    private Link link(Document doc) {
        return new Link( doc.get("_id").toString(),
                doc.getString("url"),
                doc.getString("description"),
                doc.getString("postedBy"));
    }

    //builds a Bson from a LinkFilter
    private Bson buildFilter(LinkFilter filter) {
        String descriptionPattern = filter.getDescriptionContains();
        String urlPattern = filter.getUrlContains();
        Bson descriptionCondition = null;
        Bson urlCondition = null;
        if (descriptionPattern != null && !descriptionPattern.isEmpty()) {
            descriptionCondition = regex("description", ".*" + descriptionPattern + ".*", "i");
        }
        if (urlPattern != null && !urlPattern.isEmpty()) {
            urlCondition = regex("url", ".*" + urlPattern + ".*", "i");
        }
        if (descriptionCondition != null && urlCondition != null) {
            return and(descriptionCondition, urlCondition);
        }
        return descriptionCondition != null ? descriptionCondition : urlCondition;
    }

    public void saveLink(Link link) {
        Document doc = new Document();
        doc.append("url", link.getUrl());
        doc.append("description", link.getDescription());
        doc.append("postedBy", link.getUserId());
        links.insertOne(doc);
    }
}
