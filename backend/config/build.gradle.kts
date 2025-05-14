plugins {
    id("java")
}

group = "com.infobank"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commons"))
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}