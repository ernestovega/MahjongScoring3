package domain.model.enums

enum class TableWinds(val index: Int) {
    NONE(-1),
    EAST(0),
    SOUTH(1),
    WEST(2),
    NORTH(3);

    companion object {
        fun from(index: Int?): TableWinds? =
            when (index) {
                EAST.index -> EAST
                SOUTH.index -> SOUTH
                WEST.index -> WEST
                NORTH.index -> NORTH
                null -> null
                else -> NONE
            }

        val asArray = arrayOf(EAST, SOUTH, WEST, NORTH)
    }
}
