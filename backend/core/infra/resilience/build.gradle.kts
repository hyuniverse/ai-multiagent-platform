plugins {
    id("java")
}

group = "com.infobank"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":global-utils"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}