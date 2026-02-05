plugins {
  // The bootRun task is a specific piece of logic defined inside the class
  // org.springframework.boot.gradle.plugin.SpringBootPlugin.
  alias(libs.plugins.spring.boot)
  id("java-quality")
}

dependencies { implementation(libs.spring.boot.starter.web) }

// skip the plain jar
tasks.getByName<Jar>("jar") { enabled = false }

tasks.bootJar { archiveFileName.set("app.jar") }
