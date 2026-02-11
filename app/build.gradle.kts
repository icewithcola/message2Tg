import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.text.SimpleDateFormat
import java.util.Date

val generateVersionKt = tasks.register("generateVersionKt") {
  val outputDir = layout.projectDirectory.dir("src/main/java/uk/kagurach/message2TG/gen")
  val outputFile = file("$outputDir/Version.kt")

  outputs.file(outputFile)

  // 扩展函数用于执行 shell 命令
  fun String.runCommand(): String = ProcessBuilder(split(" "))
    .redirectOutput(ProcessBuilder.Redirect.PIPE)
    .start().inputStream.bufferedReader().readText().trim()

  doLast {
    val commitId = "git rev-parse --short HEAD".runCommand()
    val buildTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

    val versionKtContent = """
            package uk.kagurach.message2TG.gen

            object Version {
                const val BUILD_TIME = "$buildTime"
                const val GIT_COMMIT_ID = "$commitId"
            }
        """.trimIndent()

    outputFile.writeText(versionKtContent)
  }
}

// 确保在编译前生成 Version.kt
tasks.named("preBuild") {
  dependsOn(generateVersionKt)
}


plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.compose.compiler)

  id("com.google.devtools.ksp")
}

android {
  namespace = "uk.kagurach.message2TG"
  compileSdk = 36

  defaultConfig {
    applicationId = "uk.kagurach.message2TG"
    minSdk = 27
    targetSdk = 36
    versionName = "1.6.2"
    versionCode = versionName!!.replace(".","").toInt()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }
  signingConfigs{
    create("release"){
      storeFile = File(projectDir, "key.jks")
      gradleLocalProperties(rootDir, providers).apply {
        storePassword = getProperty("keyStorePassword", System.getenv("KEYSTORE_PASS"))
        keyAlias = getProperty("keyAlias", System.getenv("ALIAS_NAME"))
        keyPassword = getProperty("keyPassword", System.getenv("ALIAS_PASS"))
      }

      enableV3Signing = true
      enableV4Signing = true
    }
  }
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  buildFeatures {
    compose = true
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  androidResources {
    @Suppress("UnstableApiUsage")
    generateLocaleConfig = true
  }
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.ui)
  implementation(libs.androidx.ui.graphics)
  implementation(libs.androidx.ui.tooling.preview)
  implementation(libs.androidx.material3)
  implementation(libs.androidx.compose.material.icons)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.work.runtime.ktx)
  implementation(libs.accompanist.systemuicontroller)

  implementation(libs.retrofit)
  implementation(libs.converter.moshi)

  implementation(libs.moshi)
  implementation(libs.moshi.kotlin)
  implementation(libs.moshi.adapters)

  ksp(libs.moshi.kotlin.codegen)

  testImplementation(libs.junit)

  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.ui.test.junit4)

  debugImplementation(libs.androidx.ui.tooling)
  debugImplementation(libs.androidx.ui.test.manifest)
}