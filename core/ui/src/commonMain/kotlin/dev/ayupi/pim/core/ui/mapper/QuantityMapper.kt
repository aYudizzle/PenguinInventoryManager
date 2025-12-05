package dev.ayupi.pim.core.ui.mapper

import dev.ayupi.pim.core.model.StorageUnit
import dev.ayupi.pim.core.ui.model.DisplayableQuantity

internal fun mapQuantityToDisplayableModel(
    quantity: Long,
    unit: StorageUnit,
    sizePerUnit: Int,
): DisplayableQuantity {
    val isPiece = unit == StorageUnit.PIECE

    val displayLabel = if (isPiece) {
        "$quantity ${unit.abbreviation}"
    } else {
        "$quantity x $sizePerUnit ${unit.abbreviation}"
    }

    val totalDisplay = if (isPiece) {
        null
    } else {
        val total = quantity * sizePerUnit
        // TODO("Berechnung ml in L und g in Kg? maybe?")
        "$total ${unit.abbreviation}"
    }

    return DisplayableQuantity(
        rawQuantity = quantity,
        rawSize = sizePerUnit,
        rawUnit = unit.abbreviation,
        label = displayLabel,
        totalLabel = totalDisplay
    )
}
