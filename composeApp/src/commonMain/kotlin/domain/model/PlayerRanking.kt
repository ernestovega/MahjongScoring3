package domain.model

data class PlayerRanking(
    val name: String,
    val score: Int,
    var points: String? = null,
)
