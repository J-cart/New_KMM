package com.prototype.newkmm.core

import android.content.Context
import com.prototype.newkmm.database.JournyPrototypeDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory(private val context: Context) {
   actual fun createDriver():SqlDriver = AndroidSqliteDriver(
       JournyPrototypeDatabase.Schema,
        context,
        "journy_android.db"
    )
}