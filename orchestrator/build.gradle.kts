plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "com.infobank"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":config"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

