# Building Better Microservices with Spring Boot 3

A comprehensive tutorial implementation showcasing modern microservices architecture using Spring Boot 3, demonstrating best practices for building scalable, maintainable, and production-ready microservices.

## Overview

This repository contains the complete source code for the **Product Management Microservice** tutorial from [Building Better Microservices with Spring Boot 3](https://cloudfullstack.dev/building-microservices-with-spring-boot-3/). The project demonstrates how to build a robust microservice using Spring Boot 3 with modern Java features, comprehensive testing, and production-ready configurations.

## Features

- **Modern Architecture**: Clean architecture with proper separation of concerns
- **Validation**: Comprehensive input validation using Bean Validation
- **API Documentation**: Interactive API documentation with Swagger/OpenAPI 3
- **Database Integration**: JPA/Hibernate with H2 database for development
- **Search & Filtering**: Advanced product search and filtering capabilities
- **Pagination**: Efficient pagination support for large datasets
- **Soft Delete**: Soft delete implementation for data integrity
- **Caching**: Redis-ready caching configuration
- **Monitoring**: Spring Boot Actuator for health checks and metrics
- **Testing**: Comprehensive unit and integration tests
- **MapStruct**: Type-safe mapping between DTOs and entities
- **Logging**: Structured logging with SLF4J

## Technology Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Spring Web**
- **Spring Validation**
- **Spring Boot Actuator**
- **H2 Database** (development)
- **MapStruct** (object mapping)
- **Lombok** (boilerplate reduction)
- **JUnit 5** (testing)
- **Mockito** (mocking)
- **Swagger/OpenAPI 3** (API documentation)
- **Maven** (build tool)

## Quick Start

### Prerequisites

- Java 17 or later
- Maven 3.6+
- Your favourite IDE (IntelliJ IDEA, Eclipse, VS Code)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/wynnteo/springboot3.git
cd spring-boot-microservices-tutorial
```

2. **Build the project**
```bash
mvn clean compile
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access the application**
- API Base URL: `http://localhost:8080/api/v1/products`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console`
- Actuator Health: `http://localhost:8080/actuator/health`

## API Documentation

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/products` | Create a new product |
| `GET` | `/api/v1/products` | Get all products (paginated) |
| `GET` | `/api/v1/products/{uuid}` | Get product by UUID |
| `PUT` | `/api/v1/products/{uuid}` | Update product |
| `DELETE` | `/api/v1/products/{uuid}` | Delete product (soft delete) |
| `GET` | `/api/v1/products/store/{storeId}` | Get products by store |
| `GET` | `/api/v1/products/search` | Search products by title |
| `POST` | `/api/v1/products/{uuid}/stock` | Update product stock |

### Sample Request/Response

**Create Product**
```json
POST /api/v1/products
{
  "title": "iPhone 15 Pro",
  "description": "Latest iPhone with advanced features",
  "price": 999.99,
  "storeId": "STORE-001",
  "category": "Electronics",
  "stock": 50
}
```

**Response**
```json
{
  "productUuid": "550e8400-e29b-41d4-a716-446655440000",
  "title": "iPhone 15 Pro",
  "description": "Latest iPhone with advanced features",
  "price": 999.99,
  "storeId": "STORE-001",
  "category": "Electronics",
  "stock": 50,
  "active": true,
  "created_at": "2024-01-15T10:30:00",
  "updated_at": "2024-01-15T10:30:00"
}
```

## Project Structure

```
src/
├── main/
│   ├── java/com/cloudfullstack/product/
│   │   ├── ProductApplication.java         
│   │   ├── config/
│   │   │   └── CustomInfoContributor.java   
│   │   ├── controller/
│   │   │   └── ProductController.java       
│   │   ├── dto/
│   │   │   ├── CreateProductRequest.java   
│   │   │   ├── UpdateProductRequest.java   
│   │   │   ├── ProductResponse.java        
│   │   │   └── ErrorResponse.java         
│   │   ├── entity/
│   │   │   └── Product.java                
│   │   ├── exception/
│   │   │   ├── ProductNotFoundException.java
│   │   │   ├── InsufficientStockException.java
│   │   │   └── GlobalExceptionHandler.java 
│   │   ├── mapper/
│   │   │   └── ProductMapper.java         
│   │   ├── repository/
│   │   │   └── ProductRepository.java      
│   │   └── service/
│   │       └── ProductService.java        
│   └── resources/
│       ├── application.yml                  
│       ├── application-dev.yml             
│       ├── application-prod.yml            
│       └── application-test.yml          
└── test/
    └── java/com/cloudfullstack/product/
        ├── ProductApplicationTests.java   
        └── ProductControllerIntegrationTest.java # Integration tests
```

## Testing

Run the test suite:

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report

# Run integration tests only
mvn test -Dtest="*IntegrationTest"
```

## Configuration

### Profiles

- **dev**: Development environment with H2 console enabled
- **prod**: Production environment with security hardening
- **test**: Testing environment with in-memory database

### Key Configuration Properties

```yaml
# Database Configuration
spring.datasource.url: jdbc:h2:mem:productdb
spring.jpa.hibernate.ddl-auto: create-drop

# Caching
spring.cache.type: simple
spring.cache.cache-names: products

# Actuator
management.endpoints.web.exposure.include: health,info,metrics,prometheus
```

## Deployment

### Docker

```dockerfile
FROM openjdk:17-jre-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

Build and run:
```bash
mvn clean package
docker build -t product-service .
docker run -p 8080:8080 product-service
```

### Production Considerations

- Configure external database (PostgreSQL, MySQL)
- Set up Redis for caching
- Configure logging aggregation
- Set up monitoring and alerting
- Implement security (OAuth2, JWT)
- Use service discovery (Eureka, Consul)

## Tutorial Series

This code is part of a comprehensive tutorial series on building microservices:

1. **Part 1**: [Building Better Microservices with Spring Boot 3](https://cloudfullstack.dev/building-microservices-with-spring-boot-3/)
2. **Part 2**: Service Communication & API Gateway
3. **Part 3**: Configuration Management & Service Discovery
4. **Part 4**: Security & Authentication
5. **Part 5**: Monitoring & Observability
6. **Part 6**: Testing Strategies
7. **Part 7**: Deployment & DevOps

## Acknowledgments

- [Spring Boot Team](https://spring.io/projects/spring-boot) for the excellent framework
- [CloudFullStack.dev](https://cloudfullstack.dev) for the comprehensive tutorial
- Open source community for the amazing tools and libraries


⭐ **If you found this helpful, please give it a star!** ⭐
