plugins {
    id("java")
}

group = "com.infobank"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commons"))
    implementation(project(":core:domain"))

    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))


    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Validation
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")


    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")


    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}