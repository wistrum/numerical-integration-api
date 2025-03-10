# Numerical Integration API

A Spring Boot API for comparing different numerical integration methods.

## Technologies Used

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.2-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-21-orange" alt="Java 11">
  <img src="https://img.shields.io/badge/Jakarta%20Validation-2.0-blue" alt="Jakarta Validation">
  <img src="https://img.shields.io/badge/JUnit-5-brightgreen" alt="JUnit 5">
  <img src="https://img.shields.io/badge/JAS-Math-blue" alt="JAS">
  <img src="https://img.shields.io/badge/MXParser-Math-blue" alt="MXParser">
  <img src="https://img.shields.io/badge/Bucket4j-Ratelimiting-orange" alt="Bucket4j">
</div>

## Overview

This project is a Spring Boot API designed to compare different numerical integration methods. It includes implementations for the following methods:

- **Lobatto Quadrature**
- **Trapezoidal Rule**
- **Simpson’s Rule**

The API also features error analysis, optimization, and various execution environments for performance comparison.

## Features

- Compare different numerical integration methods with a variety of input data.
- Use Jakarta Validation for input validation.
- Leverage JUnit for unit testing.
- Use JAS for equation solving and parsing.
- MXParser for mathematical parsing and evaluations.
- Bucket4j for rate-limiting API requests.

## API URL

The API is publicly hosted and accessible at:  
[numerical-integration-api-production.up.railway.app](https://numerical-integration-api-production.up.railway.app)

## Frontend Interface

The frontend application, built with Flutter, interfaces with the API for an easy-to-use user experience. You can access the frontend at:  
[chic-dasik-0c5065.netlify.app](https://chic-dasik-0c5065.netlify.app)

## Setup Instructions

To run this project locally, follow the instructions below:

1. Clone the repository:  
   `git clone <repository_url>`
2. Navigate to the project directory:  
   `cd numerical-integration-api`
3. Build the project using Maven:  
   `mvn clean install`
4. Run the Spring Boot application:  
   `mvn spring-boot:run`
5. The API will be accessible at `http://localhost:8080`.

## Contributing

Feel free to fork the repository and contribute by submitting pull requests. Please ensure that your contributions adhere to the existing coding style and include tests where applicable.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Links

- [Live Frontend](https://numerical-integration-api-production.up.railway.app)
