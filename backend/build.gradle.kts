plugins {
    id("java")
    id("org.springframework.boot") version "3.4.4" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "com.infobank"

repositories {
    mavenCentral()
}

dependencies {
    // 단위 테스트 용도 (루트 프로젝트에서 테스트할 경우)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
