plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.infobank.multiagentplatform.broker"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":commons"))
    implementation(project(":core:contract"))
    implementation(project(":core:infra:invoker"))

    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))
    implementation(platform("io.github.resilience4j:resilience4j-bom:2.0.2"))

    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-observation")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j:3.2.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
