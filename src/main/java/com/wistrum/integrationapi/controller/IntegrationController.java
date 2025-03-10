package com.wistrum.integrationapi.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.wistrum.integrationapi.model.IntegrationRequest;
import com.wistrum.integrationapi.util.NumericalIntegrator;

import io.github.bucket4j.*;


@RestController
@RequestMapping("/api/integrate")
public class IntegrationController {
	private NumericalIntegrator numericalIntegrator = new NumericalIntegrator();
	private final ConcurrentMap<String, Bucket> buckets = new ConcurrentHashMap<>();
	
	private Bucket getBucket(String ip) {
		return buckets.computeIfAbsent(ip, k -> 
        	Bucket4j.builder()
        			.addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)))) 
        			.build()
					);
	}
	
	@PostMapping
	public ResponseEntity <?> integrate
	(@RequestBody IntegrationRequest request, 
			@RequestHeader(value = "X-Forwarded-For", required = false) String ip) throws Exception{
		 if (ip == null || ip.isEmpty()) {
	            ip = "unknown"; //fallback for testing
	        }

		Bucket bucket = getBucket(ip);
		
		if(!bucket.tryConsume(1)) {
			throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, 
					"Too Many Requests - Please wait before retrying.");
		}
		double result = numericalIntegrator.integrate(request);
		return ResponseEntity.ok(result);
	}
}
