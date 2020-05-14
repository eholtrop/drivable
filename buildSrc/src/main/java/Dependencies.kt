import org.gradle.api.JavaVersion

object Versions {
    val NavComponent = "2.3.0-alpha04"
    val Kotlin = "1.3.61"
}

object Config {
    val Gradle = "com.android.tools.build:gradle:3.6.3"
    val Kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.Kotlin}"
    val MavenPublish = "com.github.dcendents:android-maven-gradle-plugin:1.5"
    val VersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.21.0"

    val MinSdk = 24
    val CompileSdk = 29
    val TargetSdk = 29
    val javaVersion = JavaVersion.VERSION_1_8
    val BuildTools = "29.0.3"

}

object Dependencies {

    val KotlinJDK = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.Kotlin}"

    val RxBindingAppCompat = "com.jakewharton.rxbinding3:rxbinding-appcompat:3.1.0"

    val AndroidXAppCompat = "androidx.appcompat:appcompat:1.1.0"
    val AndroidXKtx = "androidx.core:core-ktx:1.0.2"
    val AndroidXConstraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta4"
    val AndroidXMaterial = "com.google.android.material:material:1.2.0-alpha05"
    val AndroidXLegacySupport = "androidx.legacy:legacy-support-v4:1.0.0"
    val AndroidXRecyclerView = "androidx.recyclerview:recyclerview:1.2.0-alpha02"
    val AndroidXCardView = "androidx.cardview:cardview:1.0.0"
    val ViewPager2 = "androidx.viewpager2:viewpager2:1.0.0"

    val RxJava = "io.reactivex.rxjava2:rxjava:2.2.10"
    val RxJava3 = "io.reactivex.rxjava3:rxjava:3.0.0"
    val RxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
    val RxKotlin = "io.reactivex.rxjava2:rxkotlin:2.4.0"
    val junit = "junit:junit:4.12"
}