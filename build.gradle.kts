import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.22"
	kotlin("plugin.spring") version "1.9.22"
	kotlin("plugin.jpa") version "1.9.22"
	id("jacoco")
}

group = "com.capston"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	// JWT
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

	// Security
	implementation("org.springframework.boot:spring-boot-starter-security")

	// H2
	testImplementation("com.h2database:h2")

	// JUnit5
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "junit", module = "junit")  // JUnit4 의존성 제외
		exclude(group = "org.mockito", module = "mockito-core")  // Mockito 1.x 제외
	}

	// Mockito
	testImplementation("org.mockito:mockito-core:4.0.0")
	testImplementation("org.mockito:mockito-junit-jupiter:4.0.0")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy("jacocoTestReport") // 테스트 후에 JaCoCo 리포트 생성
}

tasks.jacocoTestReport {
	dependsOn(tasks.test) // 테스트 후에 JaCoCo 리포트 생성

	reports {
		xml.required.set(true)
		html.required.set(true)
	}
}

tasks {
	val bootJar by getting(BootJar::class) {
		archiveFileName.set("app.jar")
		exclude("**/Test*.class")
	}

	val testJar by creating(Jar::class) {
		group = "build"
		description = "Assembles a JAR archive containing the test classes."
		archiveFileName.set("app-with-tests.jar")
		from(sourceSets["main"].output)
		from(sourceSets["test"].output)

		manifest {
			attributes["Main-Class"] = "com.capston.sumnote.SumNoteApplication"
		}
	}

	val testWithJar by creating {
		group = "build"
		description = "Runs tests and creates a JAR including tests."
		dependsOn("test")
		dependsOn("jacocoTestReport")
		dependsOn(testJar)
	}
}
