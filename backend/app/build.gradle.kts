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
    implementation(project(":foundation:resilience"))
    implementation(project(":api:broker-api"))
    implementation(project(":api:orchestrator-api"))
    implementation(project(":config"))
    implementation("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // 설정 프로퍼티 메타데이터 생성기
    compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.4.4")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.4.4")

    implementation("org.springframework.boot:spring-boot-starter-actuator")

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
