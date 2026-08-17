# UOL Players API

Backend application inspired by the UOL HOST backend challenge.

The application allows players to register by providing their personal information and selecting a codename group. The
system automatically assigns an available codename retrieved from an external source.

## Features

* Player registration
* Player listing
* Automatic codename assignment
* Avengers codenames retrieved from a remote JSON source
* Justice League codenames retrieved from a remote XML source
* Prevention of duplicate codenames within the same group
* PostgreSQL persistence
* Request validation
* Global API error handling
* Unit and web layer tests

## Technologies

* Java 17
* Spring Boot
* Spring Web
* Spring Data JPA
* Hibernate
* PostgreSQL
* Bean Validation
* Jackson
* Jackson XML
* RestClient
* Maven
* JUnit 5
* Mockito
* AssertJ
* MockMvc

## Architecture

The application is organized into layers:

```text
Controller
    ↓
PlayerService
    ↓
CodenameService
    ↓
Clients / Repository
    ↓
External Sources / PostgreSQL
```

### Main Responsibilities

* `PlayerController`: exposes the HTTP endpoints.
* `PlayerService`: handles player registration and listing.
* `CodenameService`: selects an available codename according to the chosen group.
* `AvengersClient`: retrieves Avengers codenames from the remote JSON source.
* `JusticeLeagueClient`: retrieves Justice League codenames from the remote XML source.
* `PlayerRepository`: handles player persistence with PostgreSQL.
* `GlobalExceptionHandler`: centralizes API error responses.

## Business Rules

When registering a player:

1. The player provides:

    * name
    * email
    * phone (optional)
    * codename group

2. The available codename groups are:

```text
AVENGERS
JUSTICE_LEAGUE
```

3. The application retrieves the codename list from the corresponding external source.

4. Codenames already assigned to players from the same group are ignored.

5. The first available codename is automatically assigned to the player.

6. If all codenames from the selected group are already in use, the API returns:

```http
409 Conflict
```

The database also contains a unique constraint combining:

```text
codename + codename_group
```

This provides an additional protection against duplicate codename assignments within the same group.

## External Codename Sources

### Avengers

The Avengers codename list is retrieved from a remote JSON source:

```text
https://raw.githubusercontent.com/uolhost/test-backEnd-Java/master/referencias/vingadores.json
```

### Justice League

The Justice League codename list is retrieved from a remote XML source:

```text
https://raw.githubusercontent.com/uolhost/test-backEnd-Java/master/referencias/liga_da_justica.xml
```

The external sources may return their contents as `text/plain`.

Because of this, the application first retrieves the response as a `String` and then parses it manually using Jackson.

For JSON:

```text
HTTP Response
    ↓
String
    ↓
ObjectMapper
    ↓
AvengersResponse
```

For XML:

```text
HTTP Response
    ↓
String
    ↓
XmlMapper
    ↓
JusticeLeagueResponse
```

## API Endpoints

### Create Player

```http
POST /players
```

Example request:

```json
{
  "name": "Diogo",
  "email": "diogo@email.com",
  "phone": "11999999999",
  "codenameGroup": "AVENGERS"
}
```

Success response:

```http
201 Created
```

Example response:

```json
{
  "id": 1,
  "name": "Diogo",
  "email": "diogo@email.com",
  "phone": "11999999999",
  "codename": "Hulk",
  "codenameGroup": "AVENGERS"
}
```

The codename is not provided by the user. It is automatically selected by the application.

### List Players

```http
GET /players
```

Success response:

```http
200 OK
```

Example response:

```json
[
  {
    "id": 1,
    "name": "Diogo",
    "email": "diogo@email.com",
    "phone": "11999999999",
    "codename": "Hulk",
    "codenameGroup": "AVENGERS"
  },
  {
    "id": 2,
    "name": "Maria",
    "email": "maria@email.com",
    "phone": null,
    "codename": "Flash",
    "codenameGroup": "JUSTICE_LEAGUE"
  }
]
```

## Validation

The following fields are required:

* `name`
* `email`
* `codenameGroup`

The `phone` field is optional.

The email must also have a valid format.

Example invalid request:

```json
{
  "name": "",
  "email": "email-invalido",
  "codenameGroup": "AVENGERS"
}
```

Response:

```http
400 Bad Request
```

Example error response:

```json
{
  "timestamp": "2026-08-17T20:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "errors": {
    "name": "Nome é obrigatório",
    "email": "E-mail deve possuir um formato válido"
  },
  "path": "/players"
}
```

## Error Handling

The application uses a global exception handler to return structured API errors.

### Validation Error

```http
400 Bad Request
```

Returned when the request contains invalid data.

### No Codename Available

```http
409 Conflict
```

Returned when all codenames from the selected group are already assigned.

Example:

```json
{
  "timestamp": "2026-08-17T20:00:00Z",
  "status": 409,
  "error": "Conflict",
  "message": "Não há codinomes disponíveis para o grupo selecionado",
  "path": "/players"
}
```

### External Codename Source Failure

```http
502 Bad Gateway
```

Returned when the external codename source cannot be accessed or its content cannot be processed.

Example:

```json
{
  "timestamp": "2026-08-17T20:00:00Z",
  "status": 502,
  "error": "Bad Gateway",
  "message": "Não foi possível acessar a fonte de codinomes dos Vingadores",
  "path": "/players"
}
```

## Environment Variables

Create a `.env` file in the root directory of the project:

```env
DB_URL=jdbc:postgresql://localhost:5432/uol_players
DB_USERNAME=uol_app
DB_PASSWORD=your_password
```

The repository includes a `.env.example` file as a reference.

The real `.env` file must not be committed.

Example project root:

```text
uol-players/
├── .env
├── .env.example
├── pom.xml
├── README.md
└── src/
```

## Database

The project uses PostgreSQL.

Create a database named:

```text
uol_players
```

The application uses Spring Data JPA and Hibernate for persistence.

The current development configuration uses:

```properties
spring.jpa.hibernate.ddl-auto=update
```

The `Player` entity is persisted in the `players` table.

Main fields:

```text
id
name
email
phone
codename
codename_group
```

## Running the Application

### Requirements

Make sure you have installed:

* Java 17
* PostgreSQL
* Git

Maven does not need to be installed globally because the project includes Maven Wrapper.

### Clone the Repository

```bash
git clone <repository-url>
```

Enter the project directory:

```bash
cd uol-players
```

### Configure the Environment

Create your `.env` file using `.env.example` as reference.

Example:

```env
DB_URL=jdbc:postgresql://localhost:5432/uol_players
DB_USERNAME=uol_app
DB_PASSWORD=your_password
```

### Run the Tests

Linux/macOS:

```bash
./mvnw clean test
```

Windows:

```powershell
.\mvnw.cmd clean test
```

### Start the Application

Linux/macOS:

```bash
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

After startup, the API will be available at:

```text
http://localhost:8080
```

## Testing the API

You can use Postman, Insomnia or another HTTP client.

### Register a Player

```http
POST http://localhost:8080/players
```

Body:

```json
{
  "name": "Diogo",
  "email": "diogo@email.com",
  "phone": "11999999999",
  "codenameGroup": "AVENGERS"
}
```

### List Players

```http
GET http://localhost:8080/players
```

## Tests

The project contains automated tests for:

* Avengers HTTP client
* Justice League HTTP client
* JSON parsing
* XML parsing
* External source failures
* Codename selection
* Already-used codename handling
* No available codename scenario
* Player registration service
* Player listing service
* Player controller
* Request validation
* `409 Conflict` responses
* `502 Bad Gateway` responses

The tests use:

* JUnit 5
* Mockito
* AssertJ
* MockMvc
* MockRestServiceServer

Run the complete test suite with:

```powershell
.\mvnw.cmd clean test
```

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com
│   │       └── diogorocha
│   │           └── uol_players
│   │               ├── client
│   │               │   └── dto
│   │               ├── controller
│   │               ├── dto
│   │               ├── entity
│   │               ├── enums
│   │               ├── exception
│   │               ├── mapper
│   │               ├── repository
│   │               └── service
│   │
│   └── resources
│       └── application.properties
│
└── test
    └── java
        └── com
            └── diogorocha
                └── uol_players
                    ├── client
                    ├── controller
                    └── service
```

## Main Learning Goals

This project was developed to practice backend development with Java and Spring Boot.

Some of the concepts applied during development include:

* REST API development
* Layered architecture
* Dependency injection
* Constructor injection
* Spring Data JPA
* Hibernate
* PostgreSQL persistence
* Bean Validation
* DTOs
* Java Records
* Entity mapping
* External HTTP integrations
* JSON parsing
* XML parsing
* Java Streams
* Exception handling
* Global exception handlers
* Unit testing
* Mocking dependencies
* Controller testing
* HTTP client testing
* Git feature branch workflow

## Challenge Flow

The main registration flow works as follows:

```text
POST /players
      ↓
PlayerController
      ↓
PlayerService
      ↓
CodenameService
      ↓
Selected codename group
      ↓
┌──────────────────────┐
│ AVENGERS             │ → AvengersClient → JSON
│ JUSTICE_LEAGUE       │ → JusticeLeagueClient → XML
└──────────────────────┘
      ↓
Available codename
      ↓
PlayerRepository
      ↓
PostgreSQL
      ↓
201 Created
```

## Author

Developed by **Diogo Rocha** as a backend development study project using Java and Spring Boot.
