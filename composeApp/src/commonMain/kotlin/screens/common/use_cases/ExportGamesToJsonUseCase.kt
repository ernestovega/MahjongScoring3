package screens.common.use_cases

import androidx.annotation.VisibleForTesting
import screens.common.model.PortableGame
import screens.common.model.toPortableGame
import screens.common.model.UiGame
//import screens.common.model.exceptions.GamesNotFoundException
//import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
//import screens.common.use_cases.utils.writeToCsvFile
//import java.io.File

class ExportGamesToJsonUseCase(
    private val getAllGamesFlowUseCase: GetAllGamesFlowUseCase,
) {
//    suspend operator fun invoke(directory: File?): Result<Array<File>> =
//        runCatching {
//            val uiGames = getAllGamesFlowUseCase.invoke().firstOrNull() ?: throw GamesNotFoundException()
//            val jsonText = jsonFrom(uiGames)
//            val fileName = "MahjongMadrid2_DataBase".plus(".json")
//            val jsonFile = writeToCsvFile(fileName, jsonText, directory)
//            arrayOf(jsonFile)
//        }

    @VisibleForTesting
    fun jsonFrom(uiGames: List<UiGame>): String =
        Json.encodeToString(
            serializer = ListSerializer(PortableGame.serializer()),
            value = uiGames.map { uiGame -> uiGame.toPortableGame() }
        )
}