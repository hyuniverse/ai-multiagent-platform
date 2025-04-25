plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":common"))
    implementation(project(":orchestrator"))
    implementation(project(":resilience"))
    testImplementation(project(":domain"))
    testImplementation(project(":common"))
    testImplementation(project(":orchestrator"))

    // Lombok 추가
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    implementation("org.springframework:spring-web:6.1.4")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
