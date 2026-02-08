plugins {
  id("io.freefair.lombok") version "9.2.0"
  id("java-quality")
}

dependencies {
  implementation(libs.spring.boot.starter.web)
  implementation(libs.spring.boot.starter.data.jpa)
  implementation(libs.spring.boot.starter.data.redis)
  implementation(libs.spring.boot.starter.validation)
}
