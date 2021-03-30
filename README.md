# Aplicación FutGol TFG
[![Build Status](https://travis-ci.org/spring-petclinic/spring-petclinic-kotlin.png?branch=master)](https://travis-ci.org/spring-petclinic/spring-petclinic-kotlin/)

## Technologies used

* Language: Kotlin
* Core framework: Spring Boot 2 with Spring Framework 5 Kotlin support
* Server: Netty
* Web framework: Spring MVC
* Templates: Thymeleaf and Bootstrap
* Persistence : Spring Data JPA
* Database: MYSQL
* Build: Gradle Script with the Kotlin DSL
* Testing: Junit 5, Mockito and AssertJ

## Import and run the project in IntelliJ IDEA
   
* Make sure you have at least IntelliJ IDEA 2017.2 and IDEA Kotlin plugin 1.1.60+ (menu Tools -> Kotlin -> configure Kotlin Plugin Updates -> make sure "Stable" channel is selected -> check for updates now -> restart IDE after the update)
* Import it in IDEA as a Gradle project
  * Go to the menu "File -> New -> Project from Existing Sources... "
  * Select the spring-petclinic-kotlin directory then choose "Import the project from Gradle"
  * Select the "Use gradle wrapper task configuration" radio button
* In IntelliJ IDEA, right click on PetClinicApplication.kt then "Run..." or "Debug..."
* Open http://localhost:8080/ in your browser

