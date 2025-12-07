import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinNativeCocoaPods)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mavenPublish)
}

kotlin {
    explicitApi()
    androidTarget {
        publishLibraryVariants("release", "debug")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    jvm()
    iosX64()
    iosArm64()
    iosSimulatorArm64()


    cocoapods {
        ios.deploymentTarget = "11.0"
        framework {
            baseName = "KMPAuthGoogle"
            isStatic = true
        }
        pod("GoogleSignIn")
    }



    sourceSets {

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.playServicesAuth)
            implementation(libs.android.legacy.playServicesAuth)
            implementation(libs.googleIdIdentity)
            implementation(libs.koin.android)

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(libs.koin.compose)
            implementation(libs.koin.core)
            implementation(libs.ktor.core)
            api(project(":kmpauth-core"))
        }
        jvmMain.dependencies {
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.server.html.builder)
            implementation("com.auth0:java-jwt:4.4.0") // Check for the latest version
        }
    }
}

android {
    namespace = "com.mmk.kmpauth.google"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = true
        )
    )
    coordinates(
        project.group.toString(),
        "kmpauth-google",
        project.version.toString()
    )
    pom {
        name = "KMPAuth (Toebeans Fork)"
        description = "Kotlin Multiplatform Authentication Library targeting ios and android (Toebeans fork)"
        url = "https://github.com/ToebeansApp/KMPAuth/"
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        developers {
            developer {
                name.set("Toebeans")
                email.set("hello@toebeans.life")
            }
            developer {
                name.set("Mirzamehdi Karimov")
                email.set("mirzemehdi@gmail.com")
            }
        }
        scm {
            connection.set("https://github.com/ToebeansApp/KMPAuth.git")
            url.set("https://github.com/ToebeansApp/KMPAuth")
        }
    }

    // Publishing repositories - configured via extension
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ToebeansApp/KMPAuth")
            credentials {
                username = project.extra["githubActor"] as String?
                password = project.extra["githubToken"] as String?
            }
        }
    }
}
