package com.prototype.newkmm.core

import com.prototype.newkmm.database.JournyPrototypeDatabase
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.native.NativeSqliteDriver

actual class DatabaseDriverFactory() {
   actual fun createDriver():SqlDriver = NativeSqliteDriver(
       JournyPrototypeDatabase.Schema,
        "journy_ios.db"
    )
}