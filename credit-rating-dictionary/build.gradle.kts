plugins {
    id("org.eye.cat.tmp.java-application-conventions")
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "org.cat.eye.credit.rating"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":model"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.kafka:spring-kafka")

//    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
//    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
//    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("org.cat.eye.credit.rating.dictionary.CreditRatingDictionaryApplication")
}