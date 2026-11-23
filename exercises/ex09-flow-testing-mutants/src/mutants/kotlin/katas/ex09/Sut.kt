package katas.ex09

// Factory for the system under test. In tests ALWAYS create the monitor with vitalsMonitor(),
// so `./gradlew :ex09-flow-testing-mutants:mutationTest` can swap it for the buggy versions.
fun vitalsMonitor(): VitalsMonitor = when (System.getProperty("katas.mutant")) {
    "M01" -> Mutant01()
    "M02" -> Mutant02()
    "M03" -> Mutant03()
    "M04" -> Mutant04()
    "M05" -> Mutant05()
    "M06" -> Mutant06()
    "M07" -> Mutant07()
    "M08" -> Mutant08()
    "M09" -> Mutant09()
    "M10" -> Mutant10()
    "M11" -> Mutant11()
    else -> DefaultVitalsMonitor()
}
