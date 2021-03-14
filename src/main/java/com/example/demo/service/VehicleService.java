package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.data.graphql.VehicleRepository;
import com.example.demo.data.graphql.model.Vehicle;

@Service
public class VehicleService {

	@Autowired
	private VehicleRepository vehicleRepository;

	public Vehicle createVehicle(String type, String modelCode, String brandName, String launchDate) {
		Vehicle vehicle = new Vehicle();
		vehicle.setType(type);
		vehicle.setModelCode(modelCode);
		vehicle.setBrandName(brandName);
		vehicle.setLaunchDate(LocalDate.parse(launchDate));
		return vehicleRepository.save(vehicle);
	}

	public List<Vehicle> getAllVehicles(int count) {
		return vehicleRepository.findAll().stream().limit(count).collect(Collectors.toList());
	}

	public Optional<Vehicle> getVehicle(long id) {
		return vehicleRepository.findById(id);
	}
}
