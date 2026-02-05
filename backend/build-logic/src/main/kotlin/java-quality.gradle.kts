plugins {
    id("java")
    id("com.diffplug.spotless")
}

spotless {
    java {
      target("src/**/*.java")
      googleJavaFormat().reflowLongStrings()
      removeUnusedImports()
      trimTrailingWhitespace()
      endWithNewline()
      importOrder("java", "javax", "org", "com", "", "\\#")
    }
}
