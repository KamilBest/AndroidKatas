package katas.bonus04

// In tests ALWAYS create the calculator with premiumCalculator(): mutationTest swaps the implementation.
fun premiumCalculator(): PremiumCalculator = when (System.getProperty("katas.mutant")) {
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
    "M12" -> Mutant12()
    "M13" -> Mutant13()
    else -> DefaultPremiumCalculator()
}
