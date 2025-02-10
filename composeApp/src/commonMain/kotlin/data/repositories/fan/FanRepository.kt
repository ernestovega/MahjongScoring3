package data.repositories.fan

import kotlinx.coroutines.flow.Flow
import ui.screens.help.fan.Fan

interface FanRepository{

    val getFanFlow: Flow<List<Fan>>
}