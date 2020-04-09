[![Build Status](https://travis-ci.com/etnoy/SecurityShepherd.svg?branch=dev)](https://travis-ci.com/etnoy/SecurityShepherd)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=etnoy_SecurityShepherd&metric=coverage)](https://sonarcloud.io/dashboard?id=etnoy_SecurityShepherd)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=etnoy_SecurityShepherd&metric=alert_status)](https://sonarcloud.io/dashboard?id=etnoy_SecurityShepherd)

# A rewrite of Security Shepherd

I have used Shepherd for many years as a teaching tool in our infosec courses at Linköping University. In this setting, we run a CTF as part of the courses, resulting in >200 users in a CTF running over several months. Lately, I've become tired of running into bugs and issues due to a codebase that has outgrown its initial design. I have contributed a bit to the github repository, but now I'm taking a fresh look at everything.

Right now, this is my playground to test some new tech and practice my Java coding. If you are interested in contributing, be aware that nothing in terms of API or design is decided upon.

# Issues with Security Shepherd 3.1

- Bad exception handling. Errors are often ignored, causing undefined behavior
- No connection pooling
- Lack of database connection pooling
- Lack of SSO support
- Code duplication
- Direct Object Reference Bank challenge runs out of money
- Hard to make custom categories
- Hard to customize
- Lack of code testing

# Key ideas for this rewrite

- Reactive programming paradigm
- Java backend based on Spring Boot
- REST api for backend<->frontend communication
- Angular 9 for the frontend
- MySQL database for the platform
- Spring R2DBC manages the persistence layer in a reactive way
- Auditable scoreboard. All scores are computed as a sum of the user's scores
- Spring Data JDBC for the persistence layer
- Flags can be static (i.e. reverse engineering challenges) or dynamic (i.e. for web challenges)
- High test coverage is a goal (>95%)
- JUnit5 is used as test runner

# For developers

This Security Shepherd rewrite is in an early state. We have a long way to go before non-developers will find it useful. For developers, however, here's a quick guide to getting started. Note that all of this is subject to change.

## Backend

The backend is written in Java and uses Maven and Spring Boot. Backend code is located in the /server folder. We use Eclipse as the editor. You will need to install the Lombok extension jar for code generation to work. Please install the google java style of code formatting for beautiful code. Unit tests are found in src/main/java and are started with the JUnit5 test runner. For integration testing, you need a MySQL 8 server running on localhost with an empty root password. The MySQL server currently requires you to create a database called "core" manually, otherwise the application won't start. Note that MySQL versions older than 8 are currently not supported.

## Frontend

The frontend code uses Angular and can be found in the /client folder. We recommend Visual Studio Code for frontend development.

## Continuous integration

We use Travis CI for continuous integration. Sonarcloud is used as a static code analysis tool for the backend.
