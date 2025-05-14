plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.infobank"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // JPA Auditing용 어노테이션 지원
    implementation("org.springframework.data:spring-data-commons")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // REST 예외 핸들러(@RestControllerAdvice), ResponseEntity, @Valid 등을 사용
    implementation("org.springframework.boot:spring-boot-starter-web")
    // javax/hibernate validator (@Valid, @NotNull 등)
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}