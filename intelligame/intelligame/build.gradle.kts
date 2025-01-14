import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
  id("java")
  id("org.jetbrains.kotlin.jvm") version "2.1.0"
  id("org.jetbrains.intellij.platform") version "2.2.0"
}

val v = "1.33.7"
group = "com.glycin"
version = v

repositories {
  mavenCentral()

  intellijPlatform {
    defaultRepositories()
  }
}

intellijPlatform  {
  pluginConfiguration {
    id = "intelligame"
    name = "INTELLIGAME"
    version = v

    ideaVersion {
      sinceBuild = "232"
      untilBuild = provider { null }
    }

    vendor {
      name = "Glycin"
      url = "https://github.com/glycin"
    }
  }


  publishing {}

  signing{}
}

dependencies {
  intellijPlatform{
    intellijIdeaCommunity("2024.2.3")
    bundledPlugin("com.intellij.java")
    pluginVerifier()
    zipSigner()
    testFramework(TestFrameworkType.Platform)
  }

  implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

tasks {
  // Set the JVM compatibility versions
  withType<JavaCompile> {
    sourceCompatibility = "17"
    targetCompatibility = "17"
  }
}

kotlin{
  compilerOptions {
    jvmTarget.set(JvmTarget.JVM_17)
  }
}