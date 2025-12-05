package dev.ayupi.pim.core.model

enum class StorageUnit(val displayName: String, val abbreviation: String) {
    GRAM("Gramm", "g"),
    MILLILITER("Milliliter", "ml"),

    PIECE("Stück", "Stk");

    companion object {
        fun fromString(value: String): StorageUnit {
            return entries.find {
                it.name.equals(value, ignoreCase = true) ||
                        it.abbreviation.equals(value, ignoreCase = true)
            } ?: GRAM
        }
    }
}