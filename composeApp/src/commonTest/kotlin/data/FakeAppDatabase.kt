package data

class FakeAppDatabase {
    val gamesDao: FakeGamesDao = FakeGamesDao()
    val roundsDao: FakeRoundsDao = FakeRoundsDao()
}