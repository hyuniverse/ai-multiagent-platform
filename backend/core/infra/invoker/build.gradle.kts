plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":global-utils"))

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
