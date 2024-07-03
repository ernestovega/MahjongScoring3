package screens.help.fan

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

data class Fan(
    val fanPoints: Int,
    var fanName: StringResource,
    var fanImage: DrawableResource? = null,
    var fanDescription: StringResource? = null,
)