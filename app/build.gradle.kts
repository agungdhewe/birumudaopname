plugins {
	id("com.android.application")
	id("org.jetbrains.kotlin.android")
	id("kotlin-parcelize")
}

android {
	namespace = "com.birumuda.stockopname"
	compileSdk = 34

	defaultConfig {
		applicationId = "com.birumuda.stockopname"
		minSdk = 21
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
	kotlinOptions {
		jvmTarget = "1.8"
	}
}

dependencies {

	implementation("androidx.core:core-ktx:1.9.0")
	implementation("androidx.appcompat:appcompat:1.7.1")
	implementation("com.google.android.material:material:1.13.0")
	implementation("androidx.constraintlayout:constraintlayout:2.2.1")


	// CameraX dependencies
	val camerax_version = "1.3.4"
	implementation("androidx.camera:camera-camera2:${camerax_version}")
	implementation("androidx.camera:camera-lifecycle:${camerax_version}")
	implementation("androidx.camera:camera-view:${camerax_version}")

	// ML Kit Barcode Scanning
	implementation("com.google.mlkit:barcode-scanning:17.3.0")

	testImplementation("junit:junit:4.13.2")
	androidTestImplementation("androidx.test.ext:junit:1.3.0")
	androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
