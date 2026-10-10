rootProject.name = "kt-gff2027-2d"

pluginManagement {
  includeBuild("build-logic")

  repositories {
    gradlePluginPortal()
    google()
  }
}

@Suppress("UnstableApiUsage")
gradle.lifecycle.beforeProject {
  buildscript.dependencyLocking.lockAllConfigurations()
  dependencyLocking.lockAllConfigurations()
}

include(":game")
include(":native:webgpu")
