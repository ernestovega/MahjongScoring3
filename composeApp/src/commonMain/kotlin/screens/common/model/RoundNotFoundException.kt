package screens.common.model

class RoundNotFoundException(val roundId: RoundId) : Exception() {

    override val message: String
        get() = "RoundId = $roundId -> message = ${super.message}"
}