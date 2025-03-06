package data.database.sqldelight

import app.cash.sqldelight.ColumnAdapter
import domain.model.enums.TableWinds

object TableWindsAdapter : ColumnAdapter<TableWinds, String> {

    override fun decode(databaseValue: String): TableWinds = TableWinds.from(databaseValue)

    override fun encode(value: TableWinds): String = value.name
}