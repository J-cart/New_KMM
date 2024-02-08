package com.prototype.newkmm.data

import com.prototype.newkmm.database.JournyPrototypeDatabase
import com.prototype.newkmm.domain.JournalEntry
import com.prototype.newkmm.domain.JournalEntryDataSource
import com.prototype.newkmm.domain.toJournalEntry
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class JournalEntryDataSourceImpl(db: JournyPrototypeDatabase) : JournalEntryDataSource {
    private val query = db.journal_entryQueries

    override fun getAllJournalEntry(): Flow<List<JournalEntry>> {
        val journalsFlow = query.getAllJournalEntry().asFlow().mapToList()
        return journalsFlow.map {
            it.map {journalEntryEntity ->
                journalEntryEntity.toJournalEntry()
            }
        }
    }

    override suspend fun deleteAllJournalEntry() {
        query.deleteAllJournalEntry()
    }

    override suspend fun getJournalEntryById(id: Long): JournalEntry? {
val journalQuery = query.getJournalEntryById(id).executeAsOneOrNull()
        return journalQuery?.toJournalEntry()
    }

    override suspend fun deleteJournalEntryById(id: Long) {
        query.deleteJournalEntryById(id)
    }

    override suspend fun insertJournalEntry(journalEntry: JournalEntry) {
        query.insertJournalEntry(
            id = null,
            uuid = journalEntry.uuid,
            title = journalEntry.title,
            desc = journalEntry.desc,
            audioFile = journalEntry.audioFile,
            createdAt = journalEntry.createdAt,
            updatedAt = journalEntry.updatedAt
        )
    }

}
