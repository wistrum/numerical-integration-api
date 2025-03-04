package com.wistrum.integrationapi.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import com.wistrum.integrationapi.model.IntegrationRequest;
import com.wistrum.integrationapi.util.NumericalIntegrator;


@RestController
@RequestMapping("/api/integrate")
public class IntegrationController {
	private NumericalIntegrator numericalIntegrator = new NumericalIntegrator();
	
	@PostMapping
	public ResponseEntity <?> integrate(@RequestBody IntegrationRequest request){
		try {
			double result = numericalIntegrator.integrate(request);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Integration Failed: " + e.getMessage());
		}
	}
}
