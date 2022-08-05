@file:Suppress("DSL_SCOPE_VIOLATION")

plugins {
  alias(libs.plugins.kotlin)
  alias(libs.plugins.ktlint)
  alias(libs.plugins.detekt)
}

group = "me.devgabi"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

ktlint {
  android.set(false)
  additionalEditorconfigFile.set(rootProject.file(".editorconfig"))
}

detekt {
  buildUponDefaultConfig = true
  allRules = false
  config = files("${rootProject.projectDir}/config/detekt.yml")
  baseline = file("${rootProject.projectDir}/config/baseline.xml")
}

kotlin {
  jvm {
    withJava()
    compilations.all {
      kotlinOptions.jvmTarget = "1.8"
    }
    testRuns["test"].executionTask.configure {
      useJUnitPlatform()
      testLogging.showStandardStreams = true
      testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
  }

  js(BOTH) {
    browser {
      commonWebpackConfig {
        cssSupport.enabled = true
      }
    }
  }

  val hostOs = System.getProperty("os.name")
  val isMingwX64 = hostOs.startsWith("Windows")
  val nativeTarget = when {
    hostOs == "Mac OS X" -> macosX64("native")
    hostOs == "Linux" -> linuxX64("native")
    isMingwX64 -> mingwX64("native")
    else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
  }

  sourceSets {
    val commonMain by getting
    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val jvmMain by getting
    val jvmTest by getting

    val jsMain by getting
    val jsTest by getting

    val nativeMain by getting
    val nativeTest by getting
  }
}
