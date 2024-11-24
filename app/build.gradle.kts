import com.android.tools.r8.internal.ui

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.flinkmart.mahi"
    compileSdk = 34

    buildFeatures{
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.flinkmart.mahi"
        minSdk = 24
        targetSdk = 34
        versionCode = 28
        versionName = "3.8"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.firestore)
    implementation(libs.swiperefreshlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.mediarouter)
    implementation(libs.dexter)
    implementation(libs.firebase.messaging)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.glide)
    implementation(libs.compiler)
    implementation(libs.retrofit)
    implementation(libs.converter)
    implementation(libs.volley)
    implementation(libs.tinycart)
    implementation(libs.materialsearchbar)
    implementation(libs.whynotimagecarousel)
    implementation(libs.androidadvancedwebview)
    implementation(libs.roundedimageview)
    implementation(libs.gson)
    implementation(libs.ccp)

    annotationProcessor(libs.room.compiler)
    implementation(libs.room.runtime)
    implementation(libs.circleimageview)
    implementation(libs.firebase.ui.firestore)
    implementation(libs.shimmer)

}