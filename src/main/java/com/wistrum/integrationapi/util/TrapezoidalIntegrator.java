package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationRequest;
import org.mariuszgromada.math.mxparser.*;

public class TrapezoidalIntegrator {
	IntegrationRequest request;
	
	public TrapezoidalIntegrator(IntegrationRequest request){
		this.request = request;
	}
	public double integrate() {
		String function = request.getFunction();
		double lowerBound = request.getLowerBound();
		double upperBound = request.getUpperBound();
		int intervals = request.getIntervals();
		double integral;
		
		if(lowerBound >= upperBound) {
			throw new IllegalArgumentException(
					"Lower Bound must be less than Upper Bound.");
		}
		if(intervals <= 0 || intervals > 1000000) {
			throw new IllegalArgumentException(
					"Number of Intervals msut be greater than 0 and "
					+ "less than 1.0e6");
		}
		
		double increment = (upperBound - lowerBound) / intervals;
		Argument x = new Argument("x");
		Expression expression = new Expression(function, x);
		
		x.setArgumentValue(lowerBound);
		double fa = expression.calculate();
		x.setArgumentValue(upperBound);
		double fb = expression.calculate();
		
		double sum = 0;
		double xi = lowerBound + increment;
		for(int i = 1; i < intervals; i++) {
			x.setArgumentValue(xi);
			sum += expression.calculate();
			xi += increment;
		}
		
		integral = 0.5*increment*(fa + fb + sum);
		
		return integral;
	}
}
