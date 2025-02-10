package domain.model.exceptions

import ui.common.components.RoundId

class RoundNotFoundException(val roundId: RoundId) : Exception() {

    override val message: String
        get() = "RoundId = $roundId -> message = ${super.message}"
}