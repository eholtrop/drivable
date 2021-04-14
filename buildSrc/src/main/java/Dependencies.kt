import org.gradle.api.JavaVersion

object Versions {
    const val NavComponent = "2.3.0-alpha04"
    const val Kotlin = "1.3.61"
}

object Config {
    const val Gradle = "com.android.tools.build:gradle:3.6.3"
    const val Kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin}"
    const val MavenPublish = "com.github.dcendents:android-maven-gradle-plugin:1.5"
    const val VersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.21.0"

    const val MinSdk = 24
    const val CompileSdk = 29
    const val TargetSdk = 29
    val javaVersion = JavaVersion.VERSION_1_8
    const val BuildTools = "29.0.3"

}

object Dependencies {

    const val KotlinJDK = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.Kotlin}"

    const val RxBindingAppCompat = "com.jakewharton.rxbinding4:rxbinding-appcompat:4.0.0"

    const val AndroidXAppCompat = "androidx.appcompat:appcompat:1.1.0"

    const val RxJava = "io.reactivex.rxjava3:rxjava:3.0.0"
    const val RxKotlin = "io.reactivex.rxjava3:rxkotlin:3.0.1"
    const val junit = "junit:junit:4.12"
}