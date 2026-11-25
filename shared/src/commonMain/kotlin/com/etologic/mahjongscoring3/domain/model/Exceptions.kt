package com.etologic.mahjongscoring3.domain.model

class GameNotFoundException(val gameId: GameId) : Exception("GameId = $gameId")
class GamesNotFoundException : Exception()
class GameRoundsNumberExceededException : Exception()
class JsonGamesNotValidException(cause: Throwable? = null) : Exception(cause)
class RankingDataGenerationException : Exception()
