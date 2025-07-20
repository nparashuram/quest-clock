plugins {
    id("com.android.application")
}

android {
    namespace = "com.nparashuram.quest.clock"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.nparashuram.quest.clock"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    // Disable unused features
    buildFeatures {
        buildConfig = false
        viewBinding = false
        dataBinding = false
    }
    
    // Optimize APK
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/license.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/notice.txt"
            excludes += "META-INF/ASL2.0"
            excludes += "META-INF/*.kotlin_module"
            excludes += "META-INF/androidx.*"
            excludes += "META-INF/proguard/*"
            excludes += "META-INF/versions/*"
            excludes += "META-INF/version-control-info.textproto"
            excludes += "assets/dexopt/*"
            excludes += "DebugProbesKt.bin"
        }
    }
}

dependencies {
    // Minimal dependencies - only what's absolutely necessary
    implementation("androidx.appcompat:appcompat:1.6.1")
} 