package screens.common.model.enums

enum class TableWinds(val code: Int) {
    NONE(-1),
    EAST(0),
    SOUTH(1),
    WEST(2),
    NORTH(3);

    companion object {
        fun from(code: Int?): TableWinds? =
            when (code) {
                EAST.code -> EAST
                SOUTH.code -> SOUTH
                WEST.code -> WEST
                NORTH.code -> NORTH
                null -> null
                else -> NONE
            }

        val asArray = arrayOf(EAST, SOUTH, WEST, NORTH)
    }
}
