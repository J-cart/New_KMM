package com.prototype.newkmm.domain

import kotlinx.coroutines.flow.Flow

interface JournalEntryDataSource {

    fun getAllJournalEntry(): Flow<List<JournalEntry>>
    suspend fun deleteAllJournalEntry()

    suspend fun getJournalEntryById(id:Long): JournalEntry?

    suspend fun deleteJournalEntryById(id: Long)

    suspend fun insertJournalEntry(journalEntry: JournalEntry)
}