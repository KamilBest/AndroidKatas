package katas.ex12.domain

import katas.ex12.data.BenefitApiNames

/** Benefit name to display. */
fun BenefitType.displayName(): String = BenefitApiNames.labels.getValue(name)
