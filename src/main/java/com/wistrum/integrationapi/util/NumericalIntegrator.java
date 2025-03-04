package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.IntegrationMethod;
import com.wistrum.integrationapi.model.IntegrationRequest;

public class NumericalIntegrator {
	
	public double integrate(IntegrationRequest request) {
		if(request == null) {
			throw new IllegalArgumentException("Request cannot be null");
		}
		
		IntegrationMethod integrationMethod = request.getIntegrationMethod();
		
		switch(integrationMethod) {
		case TRAPEZOIDAL:
			TrapezoidalIntegrator trapezoidalIntegrator = new TrapezoidalIntegrator(request);
			return trapezoidalIntegrator.integrate();
			
		case SIMPSON:
			SimpsonIntegrator simpsonIntegrator = new SimpsonIntegrator(request);
			return simpsonIntegrator.integrate();
			
		case MIDPOINT:
			MidpointIntegrator midpointIntegrator = new MidpointIntegrator(request);
			return midpointIntegrator.integrate();
		case LOBATTO_QUADRATURE:
			LobattoQuadrature lq = new LobattoQuadrature(request);
			return lq.integrate();
		default:
				throw new IllegalArgumentException(
						"Unknown | Unimplemented Integration Method");
		}
	}
}
