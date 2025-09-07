plugins {
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("application")
}

group = "com.infobank"

repositories {
    mavenCentral()
}

application {
    mainClass.set("com.infobank.multiagentplatform.app.MultiAgentPlatformApplication")
}

dependencies {
    implementation(project(":api:broker-api"))
    implementation(project(":api:orchestrator-api"))
    implementation(project(":config"))
    implementation(project(":core:domain"))
    implementation(project(":core:infra:broker-client"))

    implementation("io.projectreactor.tools:blockhound:1.0.8.RELEASE")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    implementation(platform("io.github.resilience4j:resilience4j-bom:2.0.2"))

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.postgresql:r2dbc-postgresql")
    runtimeOnly("org.postgresql:postgresql") // For Flyway

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.7.0")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-aop")

    compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.4.4")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.4.4")

    implementation("io.github.resilience4j:resilience4j-spring-boot3")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-tracing")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.micrometer:micrometer-observation")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    implementation("io.micrometer:micrometer-registry-prometheus")

    developmentOnly("org.springframework.boot:spring-boot-devtools")


    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(project(":core:domain"))
    testImplementation(project(":core:infra:broker-client"))
}

tasks.test {
    useJUnitPlatform()
}
