package katas.ex12.presentation

import katas.ex12.domain.BenefitType
import katas.ex12.domain.displayName

// GIVEN
fun formatLimits(limits: Map<BenefitType, Long>): List<String> =
    limits.entries.sortedBy { it.key }.map { (type, minor) -> "${type.displayName()}: ${minor / 100},${(minor % 100).toString().padStart(2, '0')} zł" }
