package screens.help.fan.repository

import kotlinx.coroutines.flow.Flow
import screens.help.fan.model.Fan

interface FanRepository{

    val getFanFlow: Flow<List<Fan>>
}