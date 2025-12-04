package dev.ayupi.pse_new.feature.storagedetails.util

internal fun calculateHeaderIndices(
    groupedData: Map<Char, List<Any>>
): Map<Char, Int> {
    val indices = mutableMapOf<Char, Int>()
    var currentIndex = 0

    groupedData.forEach { (char, list) ->
        indices[char] = currentIndex
        // +1 für den Header selbst, + Anzahl der Items in dieser Gruppe
        currentIndex += 1 + list.size
    }

    return indices
}