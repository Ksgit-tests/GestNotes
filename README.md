# GestNotes

![Java](https://img.shields.io/badge/Java-17-orange)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20Servlet-6.0-blue)
![Tomcat](https://img.shields.io/badge/Tomcat-10.1-yellow)
![Build](https://img.shields.io/badge/build-Maven-informational)
![Status](https://img.shields.io/badge/status-work%20in%20progress-lightgrey)

A student grade management web application built with **plain Jakarta EE** (Servlet, JSP/JSTL, JDBC, DAO), without any framework, in order to master the MVC architecture from the ground up.

> **Project status:** the domain model, the data-access layer and the database initialisation are implemented and covered by automated tests. The web layer (servlets, JSP views, authentication) is the next milestone — see [Roadmap](#roadmap).

---

## Table of contents

1. [The problem](#the-problem)
2. [Features](#features)
3. [Business rules](#business-rules)
4. [Tech stack](#tech-stack)
5. [Architecture](#architecture)
6. [Database schema](#database-schema)
7. [Getting started](#getting-started)
8. [Tests](#tests)
9. [Project structure](#project-structure)
10. [Technical decisions](#technical-decisions)
11. [Challenges encountered](#challenges-encountered)
12. [Known limitations](#known-limitations)
13. [Roadmap](#roadmap)
14. [Author and context](#author-and-context)

---

## The problem

A school needs to track student results. Each student has a name, a field of study and three grades out of 20 (Mathematics, Computer Science, English) with different weights. The school needs the weighted average, the honours level ("mention") and pass/fail status of each student, plus class-level statistics, through an access-controlled web interface.

## Features

| Feature | Status |
|---|---|
| Weighted average, honours level and pass/fail computed by the model | ✅ Implemented and tested |
| Input validation (required fields, name length, grades in [0, 20], NaN rejected) | ✅ Implemented and tested |
| JDBC data-access layer (list, search, find, create, update, delete) | ✅ Implemented and tested |
| Automatic database creation and demo data at startup (listener) | ✅ Implemented |
| Student list and case-insensitive search (web) | 🚧 Planned |
| Create / edit form with server-side validation and Post-Redirect-Get | 🚧 Planned |
| Delete with confirmation and one-time "flash" message | 🚧 Planned |
| Class statistics dashboard | 🚧 Planned |
| Login / logout with session and authentication filter | 🚧 Planned |

## Business rules

| Rule | Definition |
|---|---|
| Weighted average | (Maths × 3 + Computer Science × 3 + English × 2) / 8 |
| Honours level | ≥ 16 *Très bien* · ≥ 14 *Bien* · ≥ 12 *Assez bien* · ≥ 10 *Passable* · otherwise *Ajourné* |
| Pass | average ≥ 10 |
| Validity | name required (≤ 100 characters), field of study required, each grade between 0 and 20 (NaN rejected) |

Labels are kept in French because the application targets a French-speaking school.

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Web API | Jakarta Servlet 6.0 (`provided` by the container) |
| Views (planned) | JSP 3.1 + JSTL 3.0 |
| Server | Apache Tomcat 10.1 |
| Database | H2 2.2 (in-memory, embedded — no installation required) |
| Data access | JDBC with `PreparedStatement` and try-with-resources, DAO pattern |
| Build | Maven (WAR packaging) |
| Tests | JUnit 5 |

## Architecture

Classic MVC "Model 2" architecture:

```
Browser ──HTTP──► Controller (Servlet) ──► Model (JavaBean) / DAO ──JDBC──► H2
   ▲                     │
   └──── HTML ◄── View (JSP, reads the beans through EL)
```

| Layer | Package | Responsibility |
|---|---|---|
| Model | `com.gestnotes.model` | Data and business rules (average, honours level, validation) |
| DAO | `com.gestnotes.dao` | All SQL and JDBC code, behind the `EtudiantDao` interface |
| Listener | `com.gestnotes.listener` | Creates the schema and loads demo data once at startup |
| Controller *(planned)* | `com.gestnotes.controller` | Handles HTTP requests, calls the DAO, selects the view |
| Filter *(planned)* | `com.gestnotes.filter` | Blocks access to protected pages for anonymous users |
| Views *(planned)* | `WEB-INF/views` | Display only (EL + JSTL); not directly reachable by URL |

## Database schema

```sql
CREATE TABLE etudiant (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom          VARCHAR(100) NOT NULL,
    filiere      VARCHAR(50)  NOT NULL,
    note_math    DOUBLE       NOT NULL,
    note_info    DOUBLE       NOT NULL,
    note_anglais DOUBLE       NOT NULL
);
```

The average and honours level are **not stored**: they are derived values computed by the model, so they can never be out of sync with the grades.

## Getting started

### Prerequisites

- JDK 17
- Maven 3.9+ (or the Maven integration of your IDE)
- Apache Tomcat 10.1 (Tomcat 9 will **not** work: it uses `javax.*` instead of `jakarta.*`)

### Build and test

```bash
git clone <repository-url>
cd gestnotes
mvn clean test      # compile and run the test suite
mvn clean package   # build target/gestnotes.war
```

### Run

Deploy `target/gestnotes.war` to Tomcat 10.1 (copy it into `webapps/`, or use *Run on Server* in Eclipse). At startup, the listener creates the table and inserts six demo students.

The web pages are not available yet (see [Roadmap](#roadmap)).

### Demo credentials

Once authentication is implemented, a **hard-coded demo account** will be provided for local testing only. It is a teaching simplification and **not** a production authentication mechanism.

## Tests

| Test class | Type | Covers | Result |
|---|---|---|---|
| `EtudiantTest` | Unit | weighted average, all five honours levels, pass threshold (10 vs 9.99), validation (blank name, empty field, grade > 20, grade < 0, NaN) | ✅ 4/4 passed |
| `EtudiantDaoImplTest` | Integration (real in-memory H2, no server needed) | full CRUD cycle: insert with generated key, find, update, case-insensitive search, list, delete, delete of a missing row | ✅ 1/1 passed |

**Total: 5 tests, 0 failures, 0 errors** — run with `mvn clean test` (JDK 17, Windows, Eclipse 2026-09 Maven integration).

Not yet verified: the 20 functional acceptance scenarios of the specification, which require the web layer.

## Project structure

```
gestnotes/
├── pom.xml
└── src
    ├── main
    │   ├── java/com/gestnotes
    │   │   ├── model/      Etudiant.java
    │   │   ├── dao/        EtudiantDao.java, EtudiantDaoImpl.java, ConnexionBD.java, DaoException.java
    │   │   └── listener/   InitBaseListener.java
    │   └── resources/      schema.sql, donnees.sql
    └── test/java/com/gestnotes
        ├── model/          EtudiantTest.java
        └── dao/            EtudiantDaoImplTest.java
```

## Technical decisions

| Decision | Rationale |
|---|---|
| No framework (no Spring, no Hibernate) | The goal is to understand what frameworks automate: request handling, sessions, JDBC resources. |
| Business rules in the model | One single source of truth: any future consumer (view, CSV export, API) reuses `getMoyenne()` and `valider()`. |
| `valider()` returns a list of messages instead of throwing | A form must display **all** errors at once, not only the first one. |
| Explicit `Double.isNaN` check | Every comparison with NaN is false, so a range check alone would silently accept it. |
| DAO interface + JDBC implementation | Controllers depend on the interface; the JDBC implementation can be replaced (e.g. by JPA) without changing them. |
| `PreparedStatement` everywhere | User input is sent as a parameter, never concatenated into SQL: prevents SQL injection. |
| Unchecked `DaoException` wrapping `SQLException` | Upper layers do not depend on JDBC types. |
| H2 in-memory | Zero installation for anyone cloning the repository; the DAO test runs without a server. |
| Servlet API in `provided` scope | Tomcat supplies it; bundling a second copy in the WAR causes class-loading conflicts. |

## Challenges encountered

- **Code copied from a PDF:** typographic quotes and line breaks introduced invisible compilation errors (`unclosed character literal`) and would have corrupted SQL data. Lesson: never copy code from a PDF without reviewing it.
- **Package / folder mismatch:** a test placed in the wrong package could not see the class under test. Tests now mirror the main package structure.
- **Stale compiled classes:** an old `.class` file left in `target/` was still executed by Maven. Lesson: `target/` is generated — run `mvn clean` when behaviour is inconsistent, and never commit it.

## Known limitations

- The SQL script runner splits on `;`, which would break on a `;` inside a string value. A migration tool such as Flyway would be used in a real project.
- One database connection is opened per operation through `DriverManager`. A production deployment would use a connection pool (`DataSource` configured in Tomcat).
- The in-memory database is reset at every restart (intended for a demo).

## Roadmap

- [x] Domain model and business rules, with unit tests
- [x] JDBC DAO with integration test
- [x] Database initialisation listener
- [ ] Student list and search (controller + JSP)
- [ ] Create / edit form, validation display, Post-Redirect-Get
- [ ] Delete with confirmation and flash message
- [ ] Class statistics
- [ ] Authentication: session, filter, logout
- [ ] Error pages, CSS, security review (XSS escaping with `c:out`)
- [ ] Screenshots and detailed documentation (`docs/`)
- [ ] Continuous integration (GitHub Actions running `mvn verify`)

## Author and context

**kssh** — engineering student in Data Science - AI.

This project is built step by step from a guided academic specification (Jakarta EE mini-project *GestNotes*), with the objective of understanding and being able to justify every layer of the application.

No license has been chosen yet, pending clarification of the rights attached to the original specification.
