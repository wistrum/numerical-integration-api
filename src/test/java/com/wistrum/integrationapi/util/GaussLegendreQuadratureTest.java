package com.wistrum.integrationapi.util;

import com.wistrum.integrationapi.model.AngularMeasure;
import com.wistrum.integrationapi.model.IntegrationMethod;
import com.wistrum.integrationapi.model.IntegrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GaussLegendreQuadratureTest {

    private IntegrationRequest request;
    private NumericalIntegrator numericalIntegrator;

    @BeforeEach
    void setUp() {
        request = new IntegrationRequest();
        request.setFunction("e^(x^2)");
        request.setLowerBound(0);
        request.setUpperBound(1);
        request.setAngularMeasure(AngularMeasure.RADIANS);
        request.setMethod(IntegrationMethod.GAUSS_LEGENDRE_QUADRATURE);
        request.setIntervals(5000);
        numericalIntegrator = new NumericalIntegrator();
    }

    @Test
    void testValidIntegration() throws Exception {
        double result = numericalIntegrator.integrate(request);
        assertEquals(1.4626, result, 1e-5, "Integration of x^2 from 0 to 1 should be approximately 1/3");
    }
 
    @Test
    void testFunctionSyntaxValidation() {
        request.setFunction("invalid_function(");
        assertThrows(IllegalArgumentException.class, () -> numericalIntegrator.integrate(request), "Invalid function syntax should throw an exception");
    }

    @Test
    void testNegativeIntervals() {
        request.setIntervals(-5);
        assertThrows(IllegalArgumentException.class, () -> numericalIntegrator.integrate(request), "Negative intervals should throw an exception");
    }

    @Test
    void testZeroIntervals() {
        request.setIntervals(0);
        assertThrows(IllegalArgumentException.class, () -> numericalIntegrator.integrate(request), "Zero intervals should throw an exception");
    }

    @Test
    void testSingularFunction() {
        request.setFunction("1/x");
        request.setLowerBound(0);
        request.setUpperBound(1);
        assertThrows(ArithmeticException.class, () -> numericalIntegrator.integrate(request), "Integration of singular function should throw an exception");
    }

    @Test
    void testConstantFunction() throws Exception {
        request.setFunction("5");
        double result = numericalIntegrator.integrate(request);
        assertEquals(5.0, result, 1e-5, "Integration of constant function 5 from 0 to 1 should be 5");
    }
}
