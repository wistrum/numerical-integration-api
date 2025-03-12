# Numerical Integration API

A Spring Boot API for comparing different numerical integration methods.

## Technologies Used

<div align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.4.3-green" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Java-11-orange" alt="Java 11">
  <img src="https://img.shields.io/badge/Jakarta%20Validation-3.0-blue" alt="Jakarta Validation">
  <img src="https://img.shields.io/badge/JUnit-5-brightgreen" alt="JUnit 5">
  <img src="https://img.shields.io/badge/JAS-Math-blue" alt="JAS">
  <img src="https://img.shields.io/badge/MXParser-Math-blue" alt="MXParser">
  <img src="https://img.shields.io/badge/Bucket4j-Ratelimiting-orange" alt="Bucket4j">
</div>

## Overview

This project is a Spring Boot API designed to compare different numerical integration methods. It includes implementations for the following methods:

- **Lobatto Quadrature**
- **Gauss-Legendre Quadrature**
- **Trapezoidal Rule**
- **Simpson’s Rule**
- **Midpoint Method**

## Features

- Compare different numerical integration methods with a variety of input data.
- Use Jakarta Validation for input validation.
- Leverage JUnit for unit testing.
- Use JAS for equation solving and parsing.
- Utilize MXParser for mathematical parsing and evaluations.
- Implement Bucket4j for API rate limiting.

## API URL

The API is publicly hosted and accessible at:  
[**numerical-integration-api-production.up.railway.app**](https://numerical-integration-api-production.up.railway.app)

## Endpoints

### **1. Perform Numerical Integration**

**`POST /api/integrate`**  
Performs numerical integration for a given function using the specified method.

#### **Headers:**
```http
Content-Type: application/json
X-Forwarded-For: <your-ip> (optional)
```

#### **Request Body (JSON):**
```json
{
  "function": "sin(x)",
  "lowerBound": 0,
  "upperBound": 90,
  "angularMeasure": "DEGREES",
  "integrationMethod": "TRAPEZOIDAL",
  "intervals": 100
}
```

#### **Allowed Integration Methods:**
- `TRAPEZOIDAL`
- `SIMPSON`
- `MIDPOINT`
- `GAUSS_LEGENDRE_QUADRATURE`
- `LOBATTO_QUADRATURE`

#### **Allowed Angular Measures:**
- `RADIANS`
- `DEGREES`
- `GRADIANS`

#### **Response Example (Success 200 OK):**
```json
{
  "result": 0.9999794382396074
}
```

## Frontend Interface

The frontend application, built with Flutter, provides an easy-to-use interface for interacting with the API. You can access the frontend at:  
[**Live Frontend**](https://chic-dasik-0c5065.netlify.app)

## Setup Instructions

To run this project locally, follow these steps:

1. **Clone the repository:**  
   ```sh
   git clone https://github.com/wistrum/numerical-integration-api.git
   ```

2. **Navigate to the project directory:**  
   ```sh
   cd numerical-integration-api
   ```

3. **Build the project using Maven:**  
   ```sh
   mvn clean install
   ```

4. **Run the Spring Boot application:**  
   ```sh
   mvn spring-boot:run
   ```

5. **Access the API locally:**  
   The API will be available at `http://localhost:8080`.

## Contributing

Contributions are welcome! Feel free to fork the repository and submit pull requests. Please ensure that your contributions adhere to the existing coding style and include relevant test cases.

## License

This project is licensed under the **Apache License 2.0** - see the [LICENSE](LICENSE) file for details.

## Links

- **Live API**: [numerical-integration-api-production.up.railway.app](https://numerical-integration-api-production.up.railway.app)
- **Live Frontend**: [chic-dasik-0c5065.netlify.app](https://chic-dasik-0c5065.netlify.app)

