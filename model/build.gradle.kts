plugins {
    id("org.eye.cat.tmp.java-library-conventions")
}

group = "org.cat.eye.credit.rating"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-streams:3.6.1")
//    implementation("org.springframework.kafka:spring-kafka")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

//    implementation("com.fasterxml.jackson.core:jackson-core:2.16.1")
//    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
//    implementation("com.fasterxml.jackson.core:jackson-annotations:2.16.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")

    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
