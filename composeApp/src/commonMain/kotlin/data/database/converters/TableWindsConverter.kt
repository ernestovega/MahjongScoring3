package data.database.converters

import androidx.room.TypeConverter
import domain.model.enums.TableWinds
import domain.model.enums.TableWinds.EAST
import domain.model.enums.TableWinds.NONE
import domain.model.enums.TableWinds.NORTH
import domain.model.enums.TableWinds.SOUTH
import domain.model.enums.TableWinds.WEST

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
