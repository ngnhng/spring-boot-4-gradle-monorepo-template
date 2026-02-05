plugins { id("java-quality") }

dependencies { implementation(libs.spring.boot.starter.web) }

tasks.getByName<Jar>("jar") { enabled = false }
