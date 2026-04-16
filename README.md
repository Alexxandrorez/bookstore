# Book Store Service

This project implements comprehensive business logic, security standards, and advanced data handling techniques required for enterprise-level applications.

## Tech Stack

* **Java 21**
* **Spring Boot 3** (Data JPA, Security, Web, Validation)
* **PostgreSQL** (Production DB) & **H2** (In-memory testing)
* **Hibernate** (ORM) & **Maven** (Build Tool)
* **JUnit & Mockito**: Unit testing for both Controllers and Service layers.

## Core Features & Implementation

* **Spring Security & Authentication**: Stateless authentication using **BCrypt** for secure password hashing and role-based access control.
* **Searching, Pagination & Sorting**: Efficient data retrieval using Spring Data JPA Specifications, Page and Pageable interface.
* **Data Validation**: Strict input control using Bean Validation.
* **Global Exception Handling**: Centralized error management using `@ControllerAdvice`, providing localized error messages and custom exception types.
* **UI Internationalization (i18n)**: Full support for English and Ukrainian languages, covering interfaces and system messages.
* **Order Management**: Complete implementation of business functional requirements for processing book orders.
* **Logging**: Comprehensive logging strategy covering business events, security incidents, and debug-level system states.

## Architecture & Design Patterns

* **Layered Architecture**: Clear separation between Controllers, Services, and Repositories.
* **DTO Pattern**: Decoupling domain entities from the API layer for security and flexibility.
* **Database Seeding**: Automated initial data population via optimized SQL scripts.
* **Stateless Design**: Ensuring scalability and modern security standards.

##  Configuration

The system uses a dual-database approach:
* **Testing**: Embedded H2 database for rapid unit testing.
* **Production**: PostgreSQL (hosted on Supabase) via Environment Variables:
  * `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`