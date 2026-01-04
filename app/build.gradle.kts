plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    jacoco
}

android {
    namespace = "me.rohinee.banksuite"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "me.rohinee.banksuite"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("boolean", "ENABLE_LOGS", "true")
        buildConfigField("boolean", "ENABLE_ANALYTICS", "false")
    }

    // Flavor Dimensions
    flavorDimensions += "env"
    flavorDimensions += "bank"

    // Product Flavors
    productFlavors {
        // Environment flavors
        create("dev") {
            dimension = "env"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "BASE_URL", "\"https://dev.api.bank.com\"")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "false")
        }

        create("qa") {
            dimension = "env"
            applicationIdSuffix = ".qa"
            versionNameSuffix = "-qa"
            buildConfigField("String", "BASE_URL", "\"https://qa.api.bank.com\"")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "false")
        }

        create("staging") {
            dimension = "env"
            applicationIdSuffix = ".stg"
            versionNameSuffix = "-stg"
            buildConfigField("String", "BASE_URL", "\"https://stg.api.bank.com\"")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
        }

        create("preprod") {
            dimension = "env"
            applicationIdSuffix = ".pp"
            versionNameSuffix = "-pp"
            buildConfigField("String", "BASE_URL", "\"https://pp.api.bank.com\"")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
        }

        create("prod") {
            dimension = "env"
            buildConfigField("String", "BASE_URL", "\"https://api.bank.com\"")
            buildConfigField("boolean", "ENABLE_ANALYTICS", "true")
        }

        // Bank flavors
        create("bankA") {
            dimension = "bank"
            resValue("string", "app_name", "Bank A")
            buildConfigField("String", "BANK_CODE", "\"A\"")
        }

        create("bankB") {
            dimension = "bank"
            resValue("string", "app_name", "Bank B")
            buildConfigField("String", "BANK_CODE", "\"B\"")
        }

        create("bankC") {
            dimension = "bank"
            resValue("string", "app_name", "Bank C")
            buildConfigField("String", "BANK_CODE", "\"C\"")
        }
    }

    buildTypes {
        debug {
            buildConfigField("boolean", "ENABLE_LOGS", "true")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("boolean", "ENABLE_LOGS", "false")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = true
        warningsAsErrors = false
        xmlReport = true
        htmlReport = true
    }
}

// Jacoco configuration for code coverage
tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    sourceDirectories.setFrom(layout.projectDirectory.dir("src/main/java"))
    executionData.setFrom(layout.buildDirectory.file("jacoco/testDebugUnitTest.exec"))
}

tasks.withType<Test> {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.named("check") {
    dependsOn("lintDebug", "testDebugUnitTest")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Testing dependencies
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.truth)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}