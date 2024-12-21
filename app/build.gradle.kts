plugins {
    id ("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.finalproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.finalproject"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        compose = true // Kích hoạt Jetpack Compose
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3" // Sử dụng phiên bản Compose Compiler phù hợp
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {  // Move this block here
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.circleimageview)
    implementation ("com.google.android.material:material:1.8.0")
    implementation ("com.makeramen:roundedimageview:2.3.0")

    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    implementation ("com.mapbox.navigation:android:2.17.1")
    implementation ("com.mapbox.search:mapbox-search-android-ui:1.0.0-rc.6")
    implementation ("org.mongodb:mongodb-driver-sync:4.5.1")
    implementation ("org.mongodb:bson:4.5.1")

    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore:25.1.1")

    //implementation("com.google.android.gms:play-services-auth:21.3.0")
    //implementation("androidx.credentials:credentials:1.3.0")
    //implementation("androidx.credentials:credentials-play-services-auth:1.3.0")
    //implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    implementation ("androidx.core:core:1.12.0")
    implementation (libs.firebase.auth.ktx)
    implementation (libs.firebase.bom)
    implementation (libs.androidx.credentials)
    implementation (libs.androidx.credentials.play.services.auth)
    implementation (libs.googleid)
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")


    implementation("androidx.compose.ui:ui:1.5.1")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.1")
    implementation("androidx.activity:activity-compose:1.7.2")

}
