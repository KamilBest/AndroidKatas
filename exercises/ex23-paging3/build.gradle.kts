plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.paging.common)
    testImplementation(libs.paging.testing)
}
