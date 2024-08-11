# Spring Cloud Product Service

This repository contains an implementation of a microservices architecture (MSA) using Spring Cloud. The system includes product and order management features, supporting operations for managing products, orders, and user authentication.

## Technology Stack

The project is built using the following technologies:

- **Java 17**
- **Spring Cloud Eureka**
- **Spring Cloud Gateway**
- **Spring Boot Actuator** 
- **Zipkin**
- **JWT**
- **Spring Boot Web** 
- **Spring Cloud OpenFeign** 
- **Spring Security** 
- **Spring Data JPA**
- **MySQL**
- **Redis**

## Microservices Architecture
![image](https://github.com/user-attachments/assets/a1eebc76-1f24-4fed-8d37-3417ac2df697)


The project consists of five microservices, each with its specific role and responsibilities:

**Eureka Server**
   - **Description:** Acts as the service registry for service discovery.
   - **Port:** 19090

**Gateway Service**
   - **Description:** Routes incoming requests to the appropriate services.
   - **Port:** 19091

**Order Service**
   - **Description:** Manages orders, including storing order details and associated product IDs.
   - **Port:** 19092

**Product Service**
   - **Description:** Manages product information, including saving and fetching product details.
   - **Port:** 19093

**Auth Service**
 - **Description:** Handles user registration, sign-in, and validation of tokens.
 - **Port:** 19095

## API Endpoints

For most API requests, the client must include an `Authorization` header with a Bearer token, which is received after successful user registration and sign-in. 

### Authentication APIs

**Sign In**
   - **Method:** `GET`
   - **Endpoint:** `/auth/signIn?user_id={string}`
   - **Description:** Authenticates a user by `user_id` and returns a token for further requests.
   - **Response:** Returns an `AuthResponseDto` containing the authentication token.

**Register User**
   - **Method:** `POST`
   - **Endpoint:** `/auth/signUp`
   - **Description:** Registers a new user with the provided details in the request body.
   - **Request Body:** `RegisterRequestDto` containing user registration information.
   - **Response:** Returns `200 OK` on successful registration.

**Validate User**
   - **Method:** `GET`
   - **Endpoint:** `/auth/validate?userId={string}`
   - **Description:** Validates if a user exists in the system by `userId`.

### Product APIs

**Add Product**
   - **Method:** `POST`
   - **Endpoint:** `/products`
   - **Description:** Adds a new product to the system.

**List Products**
   - **Method:** `GET`
   - **Endpoint:** `/products`
   - **Description:** Retrieves a list of all products.

### Order APIs

**Add Order**
   - **Method:** `POST`
   - **Endpoint:** `/order`
   - **Description:** Creates a new order.

**Add Product to Order**
   - **Method:** `PUT`
   - **Endpoint:** `/order/{orderId}`
   - **Description:** Adds a product to an existing order.

**Get Order**
   - **Method:** `GET`
   - **Endpoint:** `/order/{orderId}`
   - **Description:** Retrieves details of a specific order.

## Importing API Definitions

You can import the `product-api.json` file into Talend API Tester for testing the product-related APIs.
