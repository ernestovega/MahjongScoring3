
package domain.use_cases.mappers

import data.database.tables.DbGame
import data.database.tables.DbRound
import domain.model.UiGame
import domain.model.UiRound

fun DbGame.toUiGame(dbRounds: List<DbRound>): UiGame =
    UiGame(
        gameId = this.gameId,
        nameP1 = this.nameP1,
        nameP2 = this.nameP2,
        nameP3 = this.nameP3,
        nameP4 = this.nameP4,
        startDate = this.startDate,
        endDate = this.endDate,
        gameName = this.gameName,
        uiRounds = dbRounds.map { dbRound ->
            UiRound(
                gameId = dbRound.gameId,
                roundId = dbRound.roundId,
                winnerInitialSeat = dbRound.winnerInitialSeat,
                discarderInitialSeat = dbRound.discarderInitialSeat,
                handPoints = dbRound.handPoints,
                penaltyP1 = dbRound.penaltyP1,
                penaltyP2 = dbRound.penaltyP2,
                penaltyP3 = dbRound.penaltyP3,
                penaltyP4 = dbRound.penaltyP4,
            )
        },
    )