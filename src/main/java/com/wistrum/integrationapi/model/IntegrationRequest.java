package com.wistrum.integrationapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class IntegrationRequest {
	@NotBlank
	private String function;
	private double lowerBound;
	private double upperBound;
	@Enumerated(EnumType.STRING)
	@NotNull
	private AngularMeasure angularMeasure;
	@Enumerated(EnumType.STRING)
	private IntegrationMethod integrationMethod;
	private int intervals;
	
	public void setFunction(String function) { this.function = function; }
	public String getFunction() { return function; }
	
	public void setLowerBound(double lowerBound) { 
		this.lowerBound = lowerBound;
	}
	public double getLowerBound() {
		return angularMeasure.toRadians(lowerBound);
	}
	
	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}
	public double getUpperBound() {
		return angularMeasure.toRadians(upperBound);
	}
	
	public void setAngularMeasure(AngularMeasure angularMeasure) { 
		this.angularMeasure = angularMeasure;}
	public AngularMeasure getAngularMeasure () {
		return angularMeasure;
	}
	
	public void setMethod(IntegrationMethod integrationMethod) {
		this.integrationMethod = integrationMethod;}
	public IntegrationMethod getIntegrationMethod() { return integrationMethod; }
	
	public void setIntervals(int intervals) {this.intervals = intervals;}
	public int getIntervals() { return intervals; }
}
