plugins {
	application
	java
	checkstyle
	jacoco
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
	id("io.freefair.lombok") version "9.0.0"
	id("org.sonarqube") version "7.0.0.6105"
}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"
description = "Task manager"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

application {
	mainClass.set("hexlet.code.AppApplication");
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

	implementation("net.datafaker:datafaker:2.0.2")
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
	implementation("org.mapstruct:mapstruct:1.6.3")
	implementation ("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")

	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.instancio:instancio-junit:5.5.1")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.2")

	runtimeOnly("com.h2database:h2")
	runtimeOnly("org.postgresql:postgresql")

	compileOnly("org.projectlombok:lombok:1.18.42")
	annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.jacocoTestReport {
	reports {
		xml.required.set(true)
	}
}

sonar {
	properties {
		property("sonar.projectKey", "qusilon_java-project-99")
		property("sonar.organization", "qusilon")
	}
}
