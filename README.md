# Task Manager API

Task Management REST API built with **Spring Boot 3**, demonstrating dual-database interaction (**PostgreSQL** for relational user data and **MongoDB** for document task storage), **JWT-based authentication**, and data isolation between users.

---

## Completed Phases

- [x] **Phase 1 — Basic REST API and MongoDB Integration**: Task CRUD operations.
- [x] **Phase 2 — Input Handling and Error management**: DTO layer and mappers,  global exception handling 
- [x] **Phase 3 — Data filtering and pagination**: Introduced pagination, dynamic filtering and sorting.
- [x] **Phase 4 — Relational Database & Security**: 
  - PostgreSQL integration for `User` entity.
  - Flyway database migrations.
  - JWT Authentication with Spring Security (`/api/auth/register`, `/api/auth/login`).
  - Task-to-User binding in MongoDB (`userId`).
  - Strict user data isolation (users can only see and manage their own tasks).
- [ ] **Phase 5 — Microservices & Messaging (Proposed/Future)**: Planned Event-Driven Architecture with Apache Kafka to stream task event history to a separate `task-events` collection.

---

## Tech Stack

* **Java**: 25
* **Framework**: Spring Boot 4.1.0 
* **Databases**: PostgreSQL (Relational), MongoDB (Document-based, NoSql)
* **Database Migration**: Flyway
* **Security**: JWT (JSON Web Tokens)
* **Build Tool**: Maven
* **Testing**: AssertJ, Mockito

---

## How to Build and Run

### Prerequisites
* Docker and Docker Compose
* JDK 21 or higher
* Maven

### 1. Start Databases using Docker Compose
Make sure Docker is running, then execute:
```bash
docker-compose up -d
```
This starts:
* PostgreSQL on port `5432`
* MongoDB on port `27017`


### 2. Build and Run the Application

In the project root folder (where `pom.xml` is located), execute:

```bash
mvn clean package
mvn spring-boot:run
```
The application will launch locally at http://localhost:8080.

### 3. Key Decisions & Architectural Trade-offs
1.Dual-Database Approach:
* Decision: Kept structured, relational user data in PostgreSQL (ACID compliance for user management/auth) and dynamic task data in MongoDB (flexible document schema).
* Trade-off: Requires managing two separate database connections and handling entity references manually (userId stored as a field in MongoDB documents rather than a hard foreign key constraint).

2.Explicit Timestamps
* Decision: Set createdAt and updatedAt explicitly inside the service layer rather than relying heavily on annotations (@CreationTimestamp / @PrePersist).
* Trade-off: Slightly more service-layer code, but guarantees predictable, fully unit-testable business logic.

3. Standard Spring Data Pagination (`Pageable`):
   * Decision: Leveraged built-in Spring Data `Page<T>` for filtering and pagination out of the box.
   * Trade-off: Spring's default `PageImpl` serializes additional metadata into the JSON response (e.g., `pageable`, `sort`, `numberOfElements`), making the payload slightly heavier.

Phase 5 Vision (Event-Driven Architecture with Kafka):
Proposed Architecture: Task Producer (Main API) publishes TaskEvent records (CREATED, UPDATED, DELETED) to Apache Kafka topics. A separate Task Consumer service consumes these events and writes audit logs into a MongoDB task_events collection.

## Future Improvements

1. **Comprehensive Integration Testing:** Add `@SpringBootTest` and Testcontainers (PostgreSQL & MongoDB) to verify full end-to-end flows alongside current unit tests.
2. **Custom Pagination Wrapper:** Introduce a lightweight custom `PageResponseDTO` to clean up the JSON output and strip redundant framework metadata.
3. **Builder Pattern for DTOs:** Refactor complex DTO mapping using the Builder pattern or Lombok `@Builder` as entity fields expand.
4. **Enhanced Security & Production Hardening:**
   * Enable HTTPS/TLS transport security.
   * Integrate external Identity Providers (OAuth2 / Oktak).
   * Implement rate limiting and CORS filters for API protection.
5. **Structured Logging & Observability:** Add distributed tracing (Micrometer / Zipkin) and structured JSON logging for better production monitoring.

### 4. API Examples & Usage Flow
* Phase 1

| Method | Endpoint | Description | Request & Response Screenshots |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/tasks` | Create a task | **Request:**<br> <img width="1532" height="818" alt="Image" src="https://github.com/user-attachments/assets/6d9ad394-55a0-43bc-a379-08f2dc5bf6bd" /><br>**Response:**<br> <img width="1917" height="1011" alt="Image" src="https://github.com/user-attachments/assets/6f77f188-2403-4f54-980f-dc6a6216e212" /> |

* Phase 2

| Method | Endpoint | Description | Request & Response Screenshots |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/tasks` | Create a task, Validation | **Request:**<br> <img width="1534" height="809" alt="Image" src="https://github.com/user-attachments/assets/763b83fc-ebdb-4683-a855-ae4eb2764096" /><br>**Response:**<br> <img width="1532" height="809" alt="Image" src="https://github.com/user-attachments/assets/fb663c6a-256a-41f0-8811-0e7b38ea2a03" /> |

* Phase 3


| Method | Endpoint | Description | Screenshot |
| :--- | :--- | :--- | :---: |
| `GET` | `/api/tasks?status=IN_PROGRESS&priority=HIGH&page=0&size=5&sort=createdAt,desc` | Get all tasks filtered | <img width="849" height="696" alt="Image" src="https://github.com/user-attachments/assets/70a04d80-2acb-460c-a7d5-d8b1c15139a0" />



* Phase 4

| Method | Endpoint | Description | Request & Response Screenshots |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/tasks` | User 1 creates a task | **Request:**<br> <img width="1534" height="817" alt="Image" src="https://github.com/user-attachments/assets/2658a7d3-9b2d-46f0-8b14-39fe5c93d23e" /><br>**Response:**<br> <img width="1534" height="812" alt="Image" src="https://github.com/user-attachments/assets/759ea94e-9a39-4b7e-8186-53715487a309" /> <br>** GET Response for user 2 trying to get tasks:**<br> <img width="1534" height="816" alt="Image" src="https://github.com/user-attachments/assets/4a19e694-0f5f-4062-8001-73c305805f9e" /> |

| Method | Endpoint | Description | Request & Response Screenshots |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/auth/login` | Create a task, Validation | **Request:**<br> <img width="1534" height="810" alt="Image" src="https://github.com/user-attachments/assets/cb3bd7cf-41b4-45af-96f7-a8aed048a86e" /><br>**Response:**<br> <img width="1534" height="813" alt="Image" src="https://github.com/user-attachments/assets/824e7a6d-32e8-4cc2-8cbb-9c25610f24a1" /> |


