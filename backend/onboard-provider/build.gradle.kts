plugins {
  // The bootRun task is a specific piece of logic defined inside the class
  // org.springframework.boot.gradle.plugin.SpringBootPlugin.
  alias(libs.plugins.spring.boot)
  id("java-quality")
  id("io.freefair.lombok") version "9.2.0"
}

dependencies {
  implementation(project(":backend:onboard-core"))
  implementation(project(":backend:onboard-registration"))
  implementation(project(":backend:onboard-loan-origination"))

  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.spring.boot.starter.security)
  implementation(libs.spring.boot.starter.amqp)
  implementation(libs.spring.boot.starter.json)
  implementation(libs.spring.boot.starter.data.redis)

  runtimeOnly(libs.postgresql)
  runtimeOnly(libs.h2)
}

// skip the plain jar
tasks.named<Jar>("jar") { enabled = false }

tasks.bootJar { archiveFileName.set("app.jar") }
