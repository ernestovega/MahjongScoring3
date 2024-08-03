package screens.common.model.exceptions

import screens.common.ui.RoundId

class RoundNotFoundException(val roundId: RoundId) : Exception() {

    override val message: String
        get() = "RoundId = $roundId -> message = ${super.message}"
}