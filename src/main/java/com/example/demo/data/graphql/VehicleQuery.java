package com.example.demo.data.graphql;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.data.graphql.model.Vehicle;
import com.example.demo.service.VehicleService;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VehicleQuery implements GraphQLQueryResolver {

	@Autowired
	private VehicleService vehicleService;

	public List<Vehicle> getVehicles(int count) {
		log.info("Providing Vehicles!");
		return vehicleService.getAllVehicles(count);
	}

	public Optional<Vehicle> getVehicle(long id) {
		log.info("Providing Vehicle " + id);
		return vehicleService.getVehicle(id);
	}
}
