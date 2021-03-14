package com.example.demo.data.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.data.graphql.model.Vehicle;
import com.example.demo.service.VehicleService;

import graphql.kickstart.tools.GraphQLMutationResolver;

@Component
public class VehicleMutation implements GraphQLMutationResolver {

	@Autowired
	private VehicleService vehicleService;

	public Vehicle createVehicle(String type, String modelCode, String brandName, String launchDate) {
		return vehicleService.createVehicle(type, modelCode, brandName, launchDate);
	}
}
