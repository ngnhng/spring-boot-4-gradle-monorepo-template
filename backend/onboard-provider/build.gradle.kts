import org.springframework.boot.gradle.tasks.run.BootRun

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
  implementation(libs.liquibase.core)

  runtimeOnly(libs.postgresql)
  runtimeOnly(libs.h2)
}

// skip the plain jar
tasks.named<Jar>("jar") { enabled = false }

tasks.bootJar { archiveFileName.set("app.jar") }

tasks.register<BootRun>("runLiquibaseMigration") {
  group = "database"
  description = "Runs Liquibase migrations once without starting the web server."

  val bootRunTask = tasks.named<BootRun>("bootRun").get()
  classpath = bootRunTask.classpath
  mainClass.set(bootRunTask.mainClass)

  args(
      "--spring.liquibase.enabled=true",
      "--spring.main.web-application-type=none",
      "--spring.rabbitmq.listener.simple.auto-startup=false",
  )

  val migrateProfiles = project.findProperty("migrateProfiles")?.toString()
  if (!migrateProfiles.isNullOrBlank()) {
    args("--spring.profiles.active=$migrateProfiles")
  }
}
