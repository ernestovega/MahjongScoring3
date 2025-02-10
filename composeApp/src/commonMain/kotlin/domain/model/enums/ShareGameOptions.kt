package domain.model.enums

enum class ShareGameOptions(val index: Int) {
    TEXT(0),
    CSV(1),
    JSON(2);

    companion object {
        fun fromIndex(index: Int) = when (index) {
            0 -> TEXT
            1 -> CSV
            2 -> JSON
            else -> throw IllegalArgumentException()
        }
    }
}