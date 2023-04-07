package com.ibm.garage.chatgpt.config;

import org.springframework.beans.factory.annotation.Value;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

//Create a MongoDB client instance
public class MongoConfig {

	@Value("${spring.data.mongodb.uri}")
	String mongoUri;

	public MongoClient createMongoClient(String mongoUri) {
		ConnectionString connectionString = new ConnectionString(mongoUri);
		MongoClientSettings settings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
		return MongoClients.create(settings);
	}

}