package katas.ex12

// GIVEN: do not change. A simple "fitness function" on imports (in a big project: Konsist, ArchUnit or Gradle modules).

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ArchitectureTest {
    private val root = File("src/main/kotlin/katas/ex12")

    private fun importsIn(layer: String): List<Pair<String, String>> =
        root.resolve(layer).walkTopDown().filter { it.extension == "kt" }.flatMap { file ->
            file.readLines().filter { it.startsWith("import ") }.map { file.name to it.removePrefix("import ").trim() }
        }.toList()

    private fun assertNoImports(layer: String, vararg forbidden: String) {
        assertTrue(root.resolve(layer).isDirectory, "Missing directory $layer (run the tests from the module directory)")
        val violations = importsIn(layer).filter { (_, imp) -> forbidden.any { imp.startsWith(it) } }
        assertTrue(violations.isEmpty(), "Layer '$layer' breaks the dependency rule:\n" + violations.joinToString("\n") { "  ${it.first}: import ${it.second}" })
    }

    @Test fun `domain depends on nothing outward`() = assertNoImports(
        "domain", "katas.ex12.data", "katas.ex12.presentation", "android", "androidx", "retrofit2", "okhttp3", "java.net", "kotlinx.serialization",
    )

    @Test fun `presentation does not touch data`() = assertNoImports("presentation", "katas.ex12.data")

    @Test fun `data does not know presentation`() = assertNoImports("data", "katas.ex12.presentation")
}
