import org.gradle.api.plugins.quality.Checkstyle
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
  id("java-quality")
  id("io.freefair.lombok") version "9.2.0"
  alias(libs.plugins.openapi.generator)
}

dependencies {
  implementation(project(":backend:onboard-core"))
  implementation(project(":backend:onboard-loan-origination"))

  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.validation)
  implementation(libs.swagger.annotations.jakarta)
  implementation(libs.jackson.databind.nullable)

  testImplementation(libs.spring.boot.starter.test)
  testImplementation(libs.archunit)
  testImplementation(libs.testcontainers.junit.jupiter)
  testImplementation(libs.testcontainers.postgresql)
  testImplementation(libs.wiremock)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val openApiSpec =
    layout.projectDirectory.file("src/main/resources/openapi/openapi-onboard-registration.yaml")
val generatedOpenApiDir = layout.buildDirectory.dir("generated/openapi")

sourceSets {
  named("main") {
    java.srcDir(generatedOpenApiDir.map { it.dir("src/main/java") })
    java.exclude("**/generated/invoker/**")
  }
}

tasks.named<GenerateTask>("openApiGenerate") {
  generatorName.set("spring")
  inputSpec.set(openApiSpec.asFile.absolutePath)
  outputDir.set(generatedOpenApiDir.get().asFile.absolutePath)
  apiPackage.set("com.onboard.registration.generated.api")
  modelPackage.set("com.onboard.registration.generated.model")
  modelNameSuffix.set("Dto")
  configOptions.set(
      mapOf(
          "useSpringBoot3" to "true",
          "useJakartaEe" to "true",
          "openApiNullable" to "false",
          "documentationProvider" to "none",
          "annotationLibrary" to "none",
          "useTags" to "true",
          "additionalModelTypeAnnotations" to "@lombok.Builder(toBuilder = true)",
          "useOneOfInterfaces" to "true",
          "generateConstructorWithAllArgs" to "true",
          "generatedConstructorWithRequiredArgs" to "true",
          "delegatePattern" to "true",
      )
  )

  typeMappings.set(
      mapOf(
          "DateTime" to "java.time.OffsetDateTime",
          "Date" to "java.time.LocalDate",
          "Instant" to "java.time.Instant",
          "LocalDateTime" to "java.time.LocalDateTime",
          "LocalTime" to "java.time.LocalTime",
          "LocalDate" to "java.time.LocalDate",
          "OffsetDateTime" to "java.time.OffsetDateTime",
      )
  )
  generateApiTests.set(false)
  generateModelTests.set(false)
  generateApiDocumentation.set(false)
  generateModelDocumentation.set(false)
}

tasks.named("compileJava") { dependsOn("openApiGenerate") }

tasks.named<Checkstyle>("checkstyleMain") {
  // Only style-check handwritten source, not generated/library code under build/.
  source = fileTree("src/main/java")
}

tasks.test { useJUnitPlatform() }
