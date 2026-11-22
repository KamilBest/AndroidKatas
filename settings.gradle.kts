pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "android-katas"

// Every folder in exercises/ with a build.gradle.kts is a module, e.g. :ex01-sealed-state-machine
file("exercises").listFiles().orEmpty()
    .filter { it.isDirectory && it.resolve("build.gradle.kts").exists() }
    .sortedBy { it.name }
    .forEach { dir ->
        include(":${dir.name}")
        project(":${dir.name}").projectDir = dir
    }
