package com.etologic.mahjongscoring3.data

import androidx.room.useReaderConnection
import androidx.sqlite.execSQL
import com.etologic.mahjongscoring3.data.database.buildAppDatabase
import com.etologic.mahjongscoring3.data.repositories.RoomGamesRepository
import com.etologic.mahjongscoring3.data.repositories.RoomRoundsRepository
import com.etologic.mahjongscoring3.domain.usecases.GetAllGamesFlowUseCase
import com.etologic.mahjongscoring3.testutil.GameReport
import com.etologic.mahjongscoring3.testutil.GoldenFiles
import com.etologic.mahjongscoring3.testutil.Goldens
import com.etologic.mahjongscoring3.testutil.PlatformTest
import com.etologic.mahjongscoring3.testutil.assertThat
import com.etologic.mahjongscoring3.testutil.testDatabaseBuilder
import com.etologic.mahjongscoring3.testutil.testFilePath
import com.etologic.mahjongscoring3.testutil.testSqliteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test

/**
 * Port of MS2 MigrationJvmTest. A version 1 database is created from MS2's exported Room schema
 * (`commonTest/resources/ms2/room_schema_1.json`), filled with data from a real v1 install, then opened
 * with MS3's Room database, which runs Migration1to2 and validates the result against schema v2.
 */
class MigrationTest : PlatformTest() {

    private fun database(schemaPath: String): JsonObject {
        val text = GoldenFiles.readOrNull(schemaPath) ?: error("Missing schema $schemaPath")
        return Json.parseToJsonElement(text).jsonObject["database"]!!.jsonObject
    }

    private val v1Schema = "../ms2/room_schema_1.json"
    private val v2Schema = "../../../../schemas/com.etologic.mahjongscoring3.data.database.AppDatabase/2.json"

    @Test
    fun schemaFilesExistForEveryVersion() {
        assertThat(database(v1Schema)["version"]!!.jsonPrimitive.content).isEqualTo("1")
        assertThat(database(v2Schema)["version"]!!.jsonPrimitive.content).isEqualTo("2")
    }

    @Test
    fun migrate1To2KeepsGamesAndRounds() = runTest {
        val path = testFilePath("migration-test.db")
        val v1 = database(v1Schema)
        testSqliteDriver().open(path).apply {
            v1["entities"]!!.jsonArray.forEach { entity ->
                val table = entity.jsonObject["tableName"]!!.jsonPrimitive.content
                execSQL(entity.jsonObject["createSql"]!!.jsonPrimitive.content.replace("\${TABLE_NAME}", table))
                entity.jsonObject["indices"]?.jsonArray?.forEach { index ->
                    execSQL(index.jsonObject["createSql"]!!.jsonPrimitive.content.replace("\${TABLE_NAME}", table))
                }
            }
            v1["setupQueries"]!!.jsonArray.forEach { execSQL(it.jsonPrimitive.content) }
            execSQL("PRAGMA user_version = 1")
            // INSERT INTO Games VALUES(gameId, nameP1, nameP2, nameP3, nameP4, startDate)
            execSQL("INSERT INTO Games VALUES(1,'Player A','Player B','Player C','Player D',1714862073965)")
            execSQL("INSERT INTO Games VALUES(3,'Player A2','Player B2','Player C2','Player D2',1714862254044)")
            // INSERT INTO Rounds VALUES(winner, discarder, handPoints, pointsP1..P4, penaltyP1..P4, roundDuration, isEnded, gameId, roundId)
            execSQL("INSERT INTO Rounds VALUES(0,-1,8,48,-16,-16,-16,0,0,0,0,450000,1,1,1)")
            execSQL("INSERT INTO Rounds VALUES(1,2,9,-8,33,-17,-8,0,0,0,0,450000,1,1,2)")
            execSQL("INSERT INTO Rounds VALUES(2,3,10,-8,-8,34,-18,0,0,0,0,450000,1,1,3)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,0,0,0,0,0,450000,1,1,4)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,0,0,0,0,0,450000,1,3,21)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,-30,0,0,0,-30,450000,1,3,22)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,20,20,20,-60,20,20,20,-60,450000,1,3,23)")
            execSQL("INSERT INTO Rounds VALUES(-1,-1,0,0,0,0,0,0,0,0,0,450000,0,3,33)")
            execSQL("INSERT INTO Combinations VALUES(1,1,'Pure double chow',0,'',0)")
            close()
        }

        // Room validates the migrated schema against version 2 when it opens the database.
        val db = buildAppDatabase(testDatabaseBuilder(path), testSqliteDriver())
        try {
            val uiGames = GetAllGamesFlowUseCase(RoomGamesRepository(db.gamesDao()), RoomRoundsRepository(db.roundsDao()))().first()
            val tables = db.useReaderConnection { connection ->
                connection.usePrepared("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name") { statement ->
                    buildList { while (statement.step()) add(statement.getText(0)) }
                }
            }
            assertThat(tables).containsNoneOf("Combinations", "Tables", "Rounds_temp")

            assertThat(uiGames.map { it.gameId }).containsExactly(3L, 1L).inOrder()
            // Migrated games are considered ended (endDate = startDate) and have no name.
            uiGames.forEach {
                assertThat(it.endDate).isEqualTo(it.startDate)
                assertThat(it.gameName).isEmpty()
            }
            Goldens.assertMatches("migration_1_to_2.txt", uiGames.joinToString("\n") { GameReport.render(it) })
        } finally {
            db.close()
        }
    }
}
