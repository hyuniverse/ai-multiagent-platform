plugins {
    id("java")
}

group = "com.infobank.multiagentplatform.orchestrator"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:contract"))
    implementation(project(":core:infra:invoker"))
    implementation(project(":core:infra:broker-client"))
    implementation(project(":commons"))
    implementation(project(":config"))

    implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")


    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))
    implementation(platform("io.github.resilience4j:resilience4j-bom:2.0.2"))

    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")


    // Validation
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j:3.2.1")


    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.25.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
