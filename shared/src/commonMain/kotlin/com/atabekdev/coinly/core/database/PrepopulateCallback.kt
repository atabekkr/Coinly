package com.atabekdev.coinly.core.database

import androidx.room3.RoomDatabase
import androidx.sqlite.SQLiteConnection
import com.atabekdev.coinly.data.category.local.defaultCategories
import kotlin.time.Clock
import kotlin.uuid.Uuid

// Seeds default categories in onOpen, not onCreate: Room does not call onCreate after a
// destructive migration, so the table would stay empty.
class PrepopulateCallback : RoomDatabase.Callback() {

    override suspend fun onOpen(connection: SQLiteConnection) {
        val categoryCount = connection.prepare("SELECT COUNT(*) FROM categories").use { statement ->
            statement.step()
            statement.getLong(0)
        }
        if (categoryCount > 0) return

        val now = Clock.System.now().toEpochMilliseconds()
        connection.prepare(
            "INSERT INTO categories (id, name, emoji, type, sortOrder, createdAt, updatedAt) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)"
        ).use { statement ->
            defaultCategories().forEachIndexed { index, category ->
                statement.bindText(1, Uuid.random().toString())
                statement.bindText(2, category.name)
                statement.bindText(3, category.emoji)
                statement.bindText(4, category.type.name)
                statement.bindLong(5, index.toLong())
                statement.bindLong(6, now)
                statement.bindLong(7, now)
                statement.step()
                statement.reset()
            }
        }
    }
}
