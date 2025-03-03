package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;

public class SimpsonIntegrator {
	IntegrationRequest request;
	
	public SimpsonIntegrator(IntegrationRequest request) {
		this.request = request;
	}
	
	public double integrate() {
		return 0.0;
	}
}
