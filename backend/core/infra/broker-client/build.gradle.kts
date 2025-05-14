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
    implementation(project(":commons"))
    implementation(project(":core:domain"))
    implementation(project(":core:contract"))
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}