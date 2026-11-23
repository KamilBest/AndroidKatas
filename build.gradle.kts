import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.hilt) apply false
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }
        extensions.configure<KotlinJvmProjectExtension> {
            compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
        }
        dependencies {
            "implementation"(libs.coroutines.core)
            "testImplementation"(libs.junit)
            "testImplementation"(libs.kotlin.test)
            "testImplementation"(libs.coroutines.test)
            "testImplementation"(libs.turbine)
        }
        configureMutants()
    }

    // Static analysis, e.g. ./gradlew :ex13-make-it-testable:detektMain
    apply(plugin = "io.gitlab.arturbosch.detekt")
    // detekt must run on the Kotlin version it was built with
    configurations.matching { it.name.startsWith("detekt") }.configureEach {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin") useVersion("2.0.21")
        }
    }
    extensions.configure<DetektExtension> {
        config.setFrom(rootProject.file("config/detekt/detekt.yml"))
        buildUponDefaultConfig = true
        source.setFrom("src/main/kotlin", "src/main/java")
    }
}

/**
 * Mutation testing for the "write tests" katas.
 *
 * A module with src/mutants/kotlin gets a `mutationTest` task. Each M<number>_*.kt file is a copy
 * of the code with one injected bug. Your tests run once per mutant (system property `katas.mutant`).
 * A mutant is killed when at least one test fails. The goal is to kill all of them.
 */
fun Project.configureMutants() {
    val mutantsDir = file("src/mutants/kotlin")
    if (!mutantsDir.isDirectory) return
    val idPattern = Regex("""^(M\d+)_""")
    val mutantFiles = mutantsDir.walkTopDown()
        .filter { it.isFile && idPattern.containsMatchIn(it.name) }
        .sortedBy { it.name }
        .toList()
    val ids = mutantFiles.map { idPattern.find(it.name)!!.groupValues[1] }
    // The first line of a mutant file, "// MUTANT: description", is printed for survivors.
    val descriptions = mutantFiles.associate { f ->
        idPattern.find(f.name)!!.groupValues[1] to
            (f.readLines().firstOrNull { it.startsWith("// MUTANT:") }?.removePrefix("// MUTANT:")?.trim() ?: f.name)
    }

    extensions.configure<KotlinJvmProjectExtension> {
        sourceSets.getByName("test").kotlin.srcDir(mutantsDir)
    }
    val testSourceSet = extensions.getByType<SourceSetContainer>().getByName("test")

    val mutantTasks = ids.map { id ->
        tasks.register<Test>("mutant$id") {
            group = "mutation"
            description = "Runs the tests against mutant $id"
            testClassesDirs = testSourceSet.output.classesDirs
            classpath = testSourceSet.runtimeClasspath
            systemProperty("katas.mutant", id)
            ignoreFailures = true
            testLogging { events() }
            reports.html.required.set(false)
            reports.junitXml.outputLocation.set(layout.buildDirectory.dir("mutation/$id"))
            outputs.upToDateWhen { false }
            shouldRunAfter(tasks.named("test"))
        }
    }

    val resultsDir = layout.buildDirectory.dir("mutation")
    tasks.register("mutationTest") {
        group = "verification"
        description = "Checks how many injected bugs your tests catch"
        dependsOn("test")
        dependsOn(mutantTasks)
        doLast {
            val root = resultsDir.get().asFile
            val failedAttr = Regex("""(failures|errors)="([1-9]\d*)"""")
            val survivors = ids.filterNot { id ->
                root.resolve(id).listFiles { f -> f.extension == "xml" }.orEmpty()
                    .any { failedAttr.containsMatchIn(it.readText()) }
            }
            println()
            println("Killed mutants: ${ids.size - survivors.size}/${ids.size}")
            if (survivors.isEmpty()) {
                println("Your tests catch every injected bug.")
            } else {
                println("Survived (your tests don't notice them):")
                survivors.forEach { println("  - $it: ${descriptions[it]}") }
            }
        }
    }
}
