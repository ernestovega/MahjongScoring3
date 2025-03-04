package data.database.room.converters

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Instant? = timestamp?.let { Instant.fromEpochMilliseconds(timestamp) }

    @TypeConverter
    fun toTimestamp(date: Instant?): Long? = date?.toEpochMilliseconds()
}