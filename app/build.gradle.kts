plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.finalproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.finalproject"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures{
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.circleimageview)




    implementation ("org.maplibre.gl:android-sdk:9.2.1")

    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.maps:google-maps-services:0.18.0")
    implementation("com.google.maps.android:android-maps-utils:2.2.3")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")


    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")

    //Mapsforge dependencies
    implementation ("org.mapsforge:mapsforge-core:0.18.0")
    implementation ("org.mapsforge:mapsforge-map:0.18.0")
    implementation ("org.mapsforge:mapsforge-map-reader:0.18.0")
    implementation ("org.mapsforge:mapsforge-themes:0.18.0")
    implementation ("net.sf.kxml:kxml2:2.3.0")
    implementation ("org.mapsforge:mapsforge-map-android:0.18.0")
    implementation ("com.caverock:androidsvg:1.4")
    implementation ("org.mapsforge:mapsforge-core:0.18.0")
    implementation ("org.mapsforge:mapsforge-poi:0.18.0")
    implementation ("org.mapsforge:mapsforge-poi-android:0.18.0")
    implementation ("org.mapsforge:sqlite-android:0.18.0")
    implementation ("org.mapsforge:sqlite-android:0.18.0:natives-armeabi-v7a")
    implementation ("org.mapsforge:sqlite-android:0.18.0:natives-arm64-v8a")
    implementation ("org.mapsforge:sqlite-android:0.18.0:natives-x86")
    implementation ("org.mapsforge:sqlite-android:0.18.0:natives-x86_64")
}
