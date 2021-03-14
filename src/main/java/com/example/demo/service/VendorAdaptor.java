package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/*
 * The class connects to both a vendor-api.
 */
@Service
public class VendorAdaptor {

	private RestTemplate restTemplate;

	// restTemplateBuilder need not be autowired - its spring's magic
	@Autowired
	public VendorAdaptor(RestTemplateBuilder restTemplateBuilder) {
		restTemplate = restTemplateBuilder.build();
	}

	public String testVendorAPI() {
		final String uri = "http://othervendor/users";
		String result = restTemplate.getForObject(uri, String.class);
		System.out.println(result);
		return result;
	}
}
