import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
}

android {
    namespace = "io.github.diluc27.chargingdot"
    compileSdk = 34

    defaultConfig {
        applicationId = "io.github.diluc27.chargingdot"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            val keystorePropsFile = rootProject.file("keystore.properties")
            val keystoreProps = Properties()
            if (keystorePropsFile.exists()) {
                FileInputStream(keystorePropsFile).use { keystoreProps.load(it) }
            }

            val storeFilePath = (System.getenv("KEYSTORE_PATH")
                ?: System.getenv("RELEASE_STORE_FILE")
                ?: keystoreProps.getProperty("storeFile"))?.trim()

            val storePasswordVal = (System.getenv("KEYSTORE_PASSWORD")
                ?: System.getenv("RELEASE_STORE_PASSWORD")
                ?: keystoreProps.getProperty("storePassword"))?.trim()

            val keyAliasVal = (System.getenv("KEY_ALIAS")
                ?: System.getenv("RELEASE_KEY_ALIAS")
                ?: keystoreProps.getProperty("keyAlias"))?.trim()

            val keyPasswordVal = (System.getenv("KEY_PASSWORD")
                ?: System.getenv("RELEASE_KEY_PASSWORD")
                ?: keystoreProps.getProperty("keyPassword"))?.trim()

            val isStrict = System.getenv("RELEASE_SIGNING_STRICT")?.toBoolean() ?: false
            if (storeFilePath != null && file(storeFilePath).exists()) {
                storeFile = file(storeFilePath)
                storePassword = storePasswordVal
                keyAlias = keyAliasVal
                keyPassword = keyPasswordVal
            } else if (isStrict) {
                throw GradleException("Release signing configuration is required but missing or invalid.")
            } else {
                // Fallback to debug signing for local/contributor builds
                val debugConfig = getByName("debug")
                storeFile = debugConfig.storeFile
                storePassword = debugConfig.storePassword
                keyAlias = debugConfig.keyAlias
                keyPassword = debugConfig.keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
        disable += "ObsoleteSdkInt"
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}
