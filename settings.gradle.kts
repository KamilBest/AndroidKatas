pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "android-katas"

// Every folder in exercises/ and bonus/ with a build.gradle.kts is a module, e.g. :ex01-sealed-state-machine
listOf("exercises", "bonus").flatMap { root -> file(root).listFiles().orEmpty().toList() }
    .filter { it.isDirectory && it.resolve("build.gradle.kts").exists() }
    .sortedBy { it.name }
    .forEach { dir ->
        include(":${dir.name}")
        project(":${dir.name}").projectDir = dir
    }
