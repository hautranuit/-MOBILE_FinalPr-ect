package com.example.finalproject;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBClient {
    private final MongoCollection<Document> potholeCollection;

    public MongoDBClient(String connectionString, String databaseName, String collectionName) {
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        potholeCollection = database.getCollection(collectionName);
    }

    public void insertPothole(double longitude, double latitude) {
        Document document = new Document("longitude", longitude)
                .append("latitude", latitude);
        potholeCollection.insertOne(document);
    }

    public MongoCollection<Document> getPotholeCollection() {
        return potholeCollection;
    }
}
