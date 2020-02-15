[![Build Status](https://travis-ci.com/etnoy/SecurityShepherd.svg?branch=dev)](https://travis-ci.com/etnoy/SecurityShepherd)
 [![Coverage](https://sonarcloud.io/api/project_badges/measure?project=etnoy_SecurityShepherd&metric=coverage)](https://sonarcloud.io/dashboard?id=etnoy_SecurityShepherd)
 [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=etnoy_SecurityShepherd&metric=alert_status)](https://sonarcloud.io/dashboard?id=etnoy_SecurityShepherd)
 
# A rewrite of Security Shepherd
I have used Shepherd for many years as a teaching tool in our infosec courses at Linköping University. In this setting, we run a CTF as part of the courses, resulting in >200 users in a CTF running over several months. Lately, I've become tired of running into bugs and issues due to a codebase that has outgrown its initial design. I have contributed a bit to the github repository, but now I'm taking a fresh look at everything.

Right now, this is my playground to test some new tech and practice my Java coding. If you are interested in contributing, be aware that nothing in terms of API or design is decided upon.

# Issues with Security Shepherd 3.1
* Bad exception handling
* Lack of database connection pooling
* Lack of SSO support
* Code duplication
* Direct Object Reference Bank challenge runs out of money
* Hard to make custom categories
* No code tests

# Ideas for this rewrite
* Spring Data JDBC for the persistence layer
* Spring Boot 
* Result keys are not needed unless hardcoded