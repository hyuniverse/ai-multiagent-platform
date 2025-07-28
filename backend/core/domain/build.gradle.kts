plugins {
    id("java")
}

group = "com.infobank.multiagentplatform.domain"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commons"))

    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
