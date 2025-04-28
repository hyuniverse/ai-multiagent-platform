plugins {
    id("java")
}

group = "com.infobank"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commons"))
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}