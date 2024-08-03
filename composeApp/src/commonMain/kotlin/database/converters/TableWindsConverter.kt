package database.converters

import androidx.room.TypeConverter
import screens.common.model.enums.TableWinds
import screens.common.model.enums.TableWinds.EAST
import screens.common.model.enums.TableWinds.NONE
import screens.common.model.enums.TableWinds.NORTH
import screens.common.model.enums.TableWinds.SOUTH
import screens.common.model.enums.TableWinds.WEST

class TableWindsConverter {
    @TypeConverter
    fun toTableWind(code: Int): TableWinds =
        when (code) {
            EAST.code -> EAST
            SOUTH.code -> SOUTH
            WEST.code -> WEST
            NORTH.code -> NORTH
            else -> NONE
        }

    @TypeConverter
    fun toCode(tableWinds: TableWinds): Int = tableWinds.code
}
