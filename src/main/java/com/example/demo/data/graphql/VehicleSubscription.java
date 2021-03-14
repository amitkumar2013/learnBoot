package com.example.demo.data.graphql;

import java.time.Duration;
import java.util.Random;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import reactor.core.publisher.Flux;

@Component
public class VehicleSubscription implements GraphQLSubscriptionResolver {

	public Publisher<String> vehiclePrice(String modelCode) {
		Random random = new Random();
		// return a random value every second - can also come from a Kafka topic
		return Flux.interval(Duration.ofSeconds(1)).map(num -> modelCode + random.nextDouble());
	}

}
