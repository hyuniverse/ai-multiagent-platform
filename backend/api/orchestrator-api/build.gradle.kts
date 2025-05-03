plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.infobank.multiagentplatform.orchestrator"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:infra:invoker"))
    implementation(project(":core:infra:broker-client"))
    implementation(project(":commons"))
    implementation(project(":foundation:resilience"))

    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")


    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

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

    // 테스트
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
