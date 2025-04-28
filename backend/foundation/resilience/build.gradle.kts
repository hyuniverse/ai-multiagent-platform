plugins {
    `java`
}

group = "com.infobank.multiagentplatform"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // BOM 임포트
    implementation(platform("io.github.resilience4j:resilience4j-bom:2.0.2"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.4.4"))

    // Resilience4j + AOP
    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // 설정 프로퍼티 메타데이터 생성기
    compileOnly("org.springframework.boot:spring-boot-configuration-processor:3.4.4")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.4.4")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}
