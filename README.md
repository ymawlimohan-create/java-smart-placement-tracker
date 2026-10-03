# Placement Management System

Spring Boot REST application to manage campus placement drives.

## Tech
Java 17, Spring Boot 3, Spring Data JPA (Hibernate), H2 / MySQL, Maven

## Run
    mvn spring-boot:run
Server: http://localhost:8080 (H2 console: /h2-console, JDBC URL jdbc:h2:mem:placementdb)

## API
| Method | URL | Purpose |
|---|---|---|
| POST | /api/students/register | Register student |
| POST | /api/students/login | Login |
| POST | /api/drives | Add drive (admin) |
| GET | /api/drives | List drives |
| GET | /api/drives/eligible/{studentId} | Drives matching student's CGPA |
| POST | /api/applications?studentId=1&driveId=1 | Apply (checks eligibility, duplicates) |
| GET | /api/applications/student/{id} | Student's applications |
| PUT | /api/applications/{id}/status?status=SELECTED | Update status |

## Sample JSON
Student: {"name":"Ravi","email":"ravi@mail.com","password":"1234","branch":"CSE","cgpa":8.2}
Drive: {"company":"TCS","role":"Developer","packageLpa":7.0,"minCgpa":7.0,"lastDate":"2026-11-15"}

## Layers
controller -> repository -> model (entities), REST + JPA.
