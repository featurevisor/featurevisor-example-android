plugins {
    id("com.android.application")
}

android {
    namespace = "com.featurevisor.example.android"
    // Android 17 API 37 is still a preview SDK.
    //noinspection GradleDependency
    compileSdk = 36

    defaultConfig {
        applicationId = "com.featurevisor.example.android"
        minSdk = 23
        //noinspection OldTargetApi
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("com.featurevisor:featurevisor-java:3.0.0")

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    testImplementation("junit:junit:4.13.2")
}
