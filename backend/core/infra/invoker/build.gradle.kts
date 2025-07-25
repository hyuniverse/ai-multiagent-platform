plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:infra:broker-client"))
    implementation(project(":commons"))
    implementation(project(":core:contract"))

    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))
    implementation(platform("io.github.resilience4j:resilience4j-bom:2.0.2"))

    // Lombok 추가
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
