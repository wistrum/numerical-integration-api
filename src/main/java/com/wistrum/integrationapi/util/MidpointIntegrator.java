package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;

public class MidpointIntegrator {
	IntegrationRequest request;
	
	public MidpointIntegrator(IntegrationRequest request) {
		this.request = request;
	}
	
	public double integrate() {
		return 0;
	}
}
