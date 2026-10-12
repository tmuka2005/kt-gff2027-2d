plugins {
  id("convention.kotlin-multiplatform")
  id("convention.detekt")
}

group = "wrapper"
base.archivesName = "wrapper-webgpu"

kotlin {
  sourceSets {
    jvmMain {
      dependencies {
        implementation(project(":native:webgpu"))
      }
    }
  }
}
