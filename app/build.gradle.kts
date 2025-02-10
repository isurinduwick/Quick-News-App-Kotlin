plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)


}


android {
    namespace = "com.example.newsagencyproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.newsagencyproject"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true  // Enable ViewBinding if you use it
    }
}

dependencies {
    // Core dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)



    // Cloudinary for image storage
    implementation("com.cloudinary:cloudinary-android:2.0.0")

    // Glide for image retrieve from cloudinary
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")



    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
