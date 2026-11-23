plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.okhttp)
    testImplementation(libs.okhttp.mockwebserver)
}
