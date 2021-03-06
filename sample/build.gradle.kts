plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdkVersion(property("COMPILE_SDK_VERSION").toString().toInt())

    defaultConfig {
        applicationId = "me.panpf.adapter.sample"
        minSdkVersion(property("MIN_SDK_VERSION").toString().toInt())
        targetSdkVersion(property("TARGET_SDK_VERSION").toString().toInt())
        versionCode = property("VERSION_CODE").toString().toInt()
        versionName = property("VERSION_NAME").toString()
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${property("KOTLIN_VERSION")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${property("KOTLINX_COROUTINES_ANDROID")}")

    implementation("androidx.core:core-ktx:${property("ANDROIDX_CORE_KTX")}")
    implementation("androidx.appcompat:appcompat:${property("ANDROIDX_APPCOMPAT")}")
    implementation("androidx.fragment:fragment:${property("ANDROIDX_FRAGMENT")}")
    implementation("androidx.constraintlayout:constraintlayout:${property("ANDROIDX_CONSTRAINTLAYOUT")}")
    implementation("com.google.android.material:material:${property("GOOGLE_MATERIAL")}")

    implementation("io.github.panpf.pagerindicator:pagerindicator:${property("PAGER_INDICATOR")}")
    implementation("io.github.panpf.sketch:sketch:${property("SKETCH_VERSION")}")
    implementation("io.github.panpf.stickyrecycleritemdecoration:stickyrecycleritemdecoration:${property("STICKY_RECYCLER_ITEM_DECORATION")}")

    implementation(project(":assembly-adapter"))
    implementation(project(":assembly-adapter-ktx"))
    implementation(project(":assembly-adapter-paging"))
}
