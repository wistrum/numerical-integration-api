package com.wistrum.integrationapi.model;

public enum AngularMeasure {
	DEGREES,
	RADIANS,
	GRADIANS;
	
	public double toRadians(double angle) {
		switch (this) {
			case DEGREES:
				return Math.toRadians(angle);
			case RADIANS:
				return angle;
			case GRADIANS:
				return angle * Math.PI / 200;
			default:
				throw new IllegalStateException("Unexpected value: " + this);
		}
	}
}