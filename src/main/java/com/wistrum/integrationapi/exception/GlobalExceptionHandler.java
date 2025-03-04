package com.wistrum.integrationapi.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	//IllegalArgumentException
	//HttpMessageNotReadableException
	//RuntimeException
	//TimeoutException
	private static final Logger Logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	@ExceptionHandler(TimeoutException.class)
	public ResponseEntity<Map<String, String>> handleTimeoutException(TimeoutException e){
		Logger.error("Timeout occured", e.getMessage());
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put("error", "Timeout");
		errorMap.put("message", e.getMessage());
		return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
				.contentType(MediaType.APPLICATION_JSON)
				.body(errorMap);
	}
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> 
	handleIllegalArgumentException(IllegalArgumentException e){
		Logger.error("Illegal argument", e.getMessage());
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put("error", "Invalid argument");
		errorMap.put("message", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.body(errorMap);
	}
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, String>> handleRunetimeException(RuntimeException e){
		Logger.error("Runtime Exception ocurred", e.getMessage());
		Map<String, String> errorMap = new HashMap<>();
		errorMap.put("error", "Runtime exception");
		errorMap.put("message", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.body(errorMap);
	}
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity <Object> handleHttpMessageNotReadableException
	(HttpMessageNotReadableException e) {
		Logger.error("HTTP message not readable", e.getMessage());
		Map<String, String> errorMap = new HashMap<String, String>();
		errorMap.put("error", "Validation Failed");
		errorMap.put("message", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.contentType(MediaType.APPLICATION_JSON)
				.body(errorMap);
	}
}
