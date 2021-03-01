// Copyright 2021 Kuei-chun Chen. All rights reserved.
package demo;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.Filters;

import org.bson.conversions.Bson;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MongoConcurrentQuery extends DocumentPrinter {
	private static int numThreads = 4;
	private static String mongodbURI = "mongodb://user:password@localhost/keyhole?authSource=admin";

	private void execute() throws InterruptedException {
		ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);

        ConnectionString connectionString = new ConnectionString(mongodbURI);
        MongoClientSettings clientSettings = MongoClientSettings.builder().applyConnectionString(connectionString).build();
		MongoClient mongoClient = MongoClients.create(clientSettings);
		logger.info(String.format("use %d concurrent threads", numThreads));
		for (int t = 0; t < numThreads; t++) {
			final Integer thread = Integer.valueOf(t);
			executor.submit(() -> {
				Bson filter = Filters.and(Filters.eq("color", "Red"), Filters.eq("_batch", thread));
				mongoClient.getDatabase("keyhole").getCollection("vehicles").find(filter).forEach(consumer);
			});
		}
		executor.awaitTermination(5, TimeUnit.SECONDS);
		executor.shutdown();
		mongoClient.close();
	}

	public static void main(String[] args) {
		SpringApplication.run(MongoConcurrentQuery.class, args);
		MongoConcurrentQuery m = new MongoConcurrentQuery();
		try {
			m.execute();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
