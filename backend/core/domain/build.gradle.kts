plugins {
    id("java")
}

group = "com.infobank.multiagentplatform.domain"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":commons"))
    implementation(project(":foundation:resilience"))
    implementation(project(":foundation:logging"))

    // ✅ BOM 적용 (버전 명시 없이 안전한 최신 버전 관리)
    implementation(enforcedPlatform("org.springframework.boot:spring-boot-dependencies:3.4.4"))

    // ✅ 필수 의존성
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // ✅ 유틸 및 검증
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    // ✅ Lombok
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    // ✅ MapStruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // ✅ 테스트
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
