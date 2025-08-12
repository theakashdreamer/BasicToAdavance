plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.skysoftsolution.basictoadavance"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.skysoftsolution.basictoadvance"
        minSdk = 24
        targetSdk = 36
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
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ViewModel and LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0")

    // CameraX (old and new versions - you should remove duplicates)
    implementation("androidx.camera:camera-core:1.3.0-alpha06")
    implementation("androidx.camera:camera-camera2:1.3.0-alpha06")
    implementation("androidx.camera:camera-lifecycle:1.3.0-alpha06")
    implementation("androidx.camera:camera-video:1.3.0-alpha06")
    implementation("androidx.camera:camera-view:1.3.0-alpha06")
    implementation("androidx.camera:camera-mlkit-vision:1.3.0-alpha06")
    implementation("androidx.camera:camera-extensions:1.3.0-alpha06")

    // Optional camera2 APIs again (likely remove this block to avoid duplicates)
    implementation("androidx.camera:camera-core:1.1.0")
    implementation("androidx.camera:camera-camera2:1.1.0")
    implementation("androidx.camera:camera-view:1.1.0")

    implementation("androidx.loader:loader:1.1.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.paging:paging-runtime:3.1.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.fragment:fragment:1.5.7")
    implementation("androidx.fragment:fragment-ktx:1.5.7")
    debugImplementation("androidx.fragment:fragment-testing:1.5.7")
    implementation("androidx.customview:customview:1.1.0")
    implementation("androidx.compose.material3:material3:1.0.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.sqlite:sqlite:2.3.1")
    implementation("androidx.sqlite:sqlite-ktx:2.3.1")
    implementation("androidx.sqlite:sqlite-framework:2.3.1")
    implementation("com.android.volley:volley:1.2.1")

    // Retrofit, GSON, OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.3")

    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("io.github.chaosleung:pinview:1.4.4")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.media:media:1.6.0")
    implementation("androidx.lifecycle:lifecycle-service:2.6.1")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("com.mesibo.api:webrtc:1.0.5")
    implementation("org.java-websocket:Java-WebSocket:1.5.2")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.12.0"))
    implementation("com.google.firebase:firebase-analytics")

    // Room
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    // TensorFlow Lite
    implementation("org.tensorflow:tensorflow-lite:2.4.0")
    implementation("org.tensorflow:tensorflow-lite:0.0.0-nightly")
    implementation("org.tensorflow:tensorflow-lite:1.12.0")

    // ML Kit
    implementation("com.google.mlkit:common:18.9.0")
    implementation("com.google.mlkit:face-detection:16.1.5")

    // Google Play Services
    implementation("com.google.android.gms:play-services-base:16.0.1")
    implementation("com.google.android.gms:play-services-identity:16.0.0")
    implementation("com.google.android.gms:play-services-auth:16.0.1")
    implementation("com.google.android.gms:play-services-auth-api-phone:16.0.0")
    implementation("com.google.android.gms:play-services-location:16.0.0")
    implementation("com.google.android.gms:play-services-vision:15.0.0")
    implementation("com.google.android.gms:play-services-maps:19.2.0")

    // MediaPipe
    implementation("com.google.mediapipe:solution-core:latest.release")
    implementation("com.google.mediapipe:facedetection:latest.release")
    implementation("com.google.mediapipe:facemesh:latest.release")
    implementation("com.google.mediapipe:hands:latest.release")

    implementation("com.jakewharton:butterknife:10.0.0")
    implementation("io.reactivex.rxjava2:rxandroid:2.0.2")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("com.squareup.picasso:picasso:2.71828")

    // Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("com.android.support.test:rules:1.0.2")
    androidTestImplementation("com.google.truth:truth:1.0.1")

    // 🔹 UI Utilities
    implementation("androidx.palette:palette-ktx:1.0.0")

    // 🔹 Navigation Components
    implementation("androidx.navigation:navigation-fragment:2.9.2")
    implementation("androidx.navigation:navigation-ui:2.9.2")

    // 🔹 Firebase
    implementation("com.google.firebase:firebase-firestore:25.1.4")

    // 🔹 Google Play Services
    implementation("com.google.android.gms:play-services-nearby:19.3.0")

    // 🔹 ML Kit Vision
    implementation("com.google.mlkit:vision-internal-vkp:18.2.3")

    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.libraries.places:places:3.2.0")
}