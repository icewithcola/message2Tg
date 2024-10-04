import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.kotlin.android)
  alias(libs.plugins.compose.compiler)

  id("com.google.devtools.ksp")
}

android {
  namespace = "uk.kagurach.message2TG"
  compileSdk = 34

  defaultConfig {
    applicationId = "uk.kagurach.message2TG"
    minSdk = 27
    targetSdk = 34
    versionName = "1.3.0" // Should be majorV.functionV.subV
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  androidResources {
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
  implementation(libs.androidx.datastore.preferences)

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