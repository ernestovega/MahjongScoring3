package screens.common.use_cases

import androidx.annotation.VisibleForTesting
import screens.common.model.PortableGame
import screens.common.model.toPortableGame
//import screens.common.model.GameId
import screens.common.model.UiGame
//import screens.common.model.exceptions.GameNotFoundException
//import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
//import screens.common.use_cases.utils.normalizeName
//import screens.common.use_cases.utils.toFileNameFormat
//import screens.common.use_cases.utils.writeToCsvFile
//import java.io.File

class ExportGameToJsonUseCase(
    private val getOneGameFlowUseCase: GetOneGameFlowUseCase,
) {
//    suspend operator fun invoke(gameId: GameId, directory: File?): Result<File> =
//        runCatching {
//            getOneGameFlowUseCase.invoke(gameId)
//                .firstOrNull()
//                ?.let { uiGame ->
//                    val portableGames = listOf(uiGame.toPortableGame())
//                    val jsonText = Json.encodeToString(ListSerializer(PortableGame.serializer()), portableGames)
//                    val fileName = normalizeName(uiGame.gameName)
//                        .replace(" ", "_")
//                        .ifEmpty { uiGame.startDate.toFileNameFormat() }
//                        .plus(".json")
//                    val jsonFile = writeToCsvFile(fileName, jsonText, directory)
//                    jsonFile
//                }
//                ?: throw GameNotFoundException(gameId)
//        }

    @VisibleForTesting
    fun jsonFrom(uiGame: UiGame): String =
        Json.encodeToString(
            serializer = ListSerializer(PortableGame.serializer()),
            value = listOf(uiGame.toPortableGame()),
        )
}