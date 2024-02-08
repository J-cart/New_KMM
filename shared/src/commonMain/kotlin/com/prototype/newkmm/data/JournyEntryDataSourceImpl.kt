package com.prototype.newkmm.data

import com.prototype.newkmm.database.JournyPrototypeDatabase
import com.prototype.newkmm.domain.JournyEntry
import com.prototype.newkmm.domain.JournyEntryDataSource
import com.prototype.newkmm.domain.toJournalEntry
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JournyEntryDataSourceImpl(db: JournyPrototypeDatabase) : JournyEntryDataSource {
    private val query = db.journal_entryQueries

    override fun getAllJournalEntry(): Flow<List<JournyEntry>> {
        val journalsFlow = query.getAllJournalEntry().asFlow().mapToList()
        return journalsFlow.map {
            it.map {journalEntryEntity ->
                journalEntryEntity.toJournalEntry()
            }
        }
    }

    override suspend fun deleteAllJournyEntry() {
        query.deleteAllJournalEntry()
    }

    override suspend fun getJournyEntryById(id: Long): JournyEntry? {
val journalQuery = query.getJournalEntryById(id).executeAsOneOrNull()
        return journalQuery?.toJournalEntry()
    }

    override suspend fun deleteJournyEntryById(id: Long) {
        query.deleteJournalEntryById(id)
    }

    override suspend fun insertJournyEntry(journyEntry: JournyEntry) {
        query.insertJournalEntry(
            id = null,
            uuid = journyEntry.uuid,
            title = journyEntry.title,
            desc = journyEntry.desc,
            audioFile = journyEntry.audioFile,
            createdAt = journyEntry.createdAt,
            updatedAt = journyEntry.updatedAt
        )
    }

}
