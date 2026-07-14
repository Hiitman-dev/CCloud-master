import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.pira.ccloud"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.pira.ccloud"
        // Supported Android versions: Android 8.0 (API 24) and higher
        // Android 7.0 (API 23) and earlier are not supported
        minSdk = 24
        targetSdk = 36
        versionCode = 21
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Add memory management options
        multiDexEnabled = true

        // ── Secrets (loaded from local.properties, never committed) ──
        // To set up: add these lines to your local.properties file:
        //   CLOUD_API_KEY=your_api_key_here
        //   CLOUD_API_BASE_URL=https://your-api-server.com
        //   CLOUD_FALLBACK_SERVER_1=https://fallback1.com
        //   CLOUD_FALLBACK_SERVER_2=https://fallback2.com
        val localProps = Properties()
        val localPropsFile = rootProject.file("local.properties")
        if (localPropsFile.exists()) {
            localProps.load(localPropsFile.inputStream())
        }
        buildConfigField(
            "String",
            "API_KEY",
            "\"${localProps.getProperty("CLOUD_API_KEY", "")}\""
        )
        buildConfigField(
            "String",
            "API_BASE_URL",
            "\"${localProps.getProperty("CLOUD_API_BASE_URL", "https://server-hi-speed-iran.info")}\""
        )
        buildConfigField(
            "String",
            "FALLBACK_SERVER_1",
            "\"${localProps.getProperty("CLOUD_FALLBACK_SERVER_1", "https://hostinnegar.com")}\""
        )
        buildConfigField(
            "String",
            "FALLBACK_SERVER_2",
            "\"${localProps.getProperty("CLOUD_FALLBACK_SERVER_2", "https://windowsdiba.info")}\""
        )
    }
    
    buildFeatures {
        compose = true
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            val keystorePropertiesFile = rootProject.file("key.properties")
            val keystoreProperties = Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            }
            
            keyAlias = keystoreProperties.getProperty("keyAlias") ?: ""
            keyPassword = keystoreProperties.getProperty("keyPassword") ?: ""
            storeFile = file(keystoreProperties.getProperty("storeFile") ?: "keystore.jks")
            storePassword = keystoreProperties.getProperty("storePassword") ?: ""
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // Move keystoreProperties declaration to correct scope
            val keystorePropertiesFile = rootProject.file("key.properties")
            val keystoreProperties = Properties()
            if (keystorePropertiesFile.exists()) {
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))
            }
            val storeFilePath = keystoreProperties.getProperty("storeFile") ?: "keystore/debug.keystore"
            signingConfig = if (file(storeFilePath).exists()) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        
        debug {
            isDebuggable = true
        }
    }
    
    splits {
        abi {
            isEnable = true
            isUniversalApk = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
    
    // Add compatibility configurations for older Android versions
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    // Add support for different screen sizes including TV
    sourceSets {
        getByName("main") {
            res {
                srcDirs("src/main/res", "src/main/res/values-television")
            }
        }
    }
    
    // Lint configuration to handle missing default resource issue
    lint {
        // Use baseline to ignore existing lint errors
        baseline = file("lint-baseline.xml")
        // Continue build even if lint errors are found
        abortOnError = false
        checkReleaseBuilds = false
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
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.coil.compose)
    
    // ExoPlayer for video playback
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.4.1")
    
    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    
    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Hilt Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Retrofit (prepared for Phase 4 data layer migration)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.retrofit.logging)
    
    // Leanback for TV support
    implementation(libs.androidx.leanback)
    implementation(libs.androidx.leanback.preference)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.mockito:mockito-core:5.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    androidTestImplementation("com.google.dagger:hilt-compiler:2.51.1")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
