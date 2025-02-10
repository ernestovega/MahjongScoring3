package domain.model.enums

enum class SeatsOrientation(val code: Int) {
    DOWN(0),
    OUT(1);

    companion object {
        fun from(code: Int?) = when (code) {
            0 -> DOWN
            1 -> OUT
            else -> DOWN
        }
    }
}
