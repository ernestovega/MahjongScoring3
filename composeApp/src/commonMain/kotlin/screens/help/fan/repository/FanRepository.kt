package screens.help.fan.repository

import kotlinx.coroutines.flow.Flow
import screens.help.fan.Fan

interface FanRepository{

    val getFanFlow: Flow<List<Fan>>
}