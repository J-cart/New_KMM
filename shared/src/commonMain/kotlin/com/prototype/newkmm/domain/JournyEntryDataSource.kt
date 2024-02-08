package com.prototype.newkmm.domain

import kotlinx.coroutines.flow.Flow

interface JournyEntryDataSource {

    fun getAllJournalEntry(): Flow<List<JournyEntry>>
    suspend fun deleteAllJournyEntry()

    suspend fun getJournyEntryById(id:Long): JournyEntry?

    suspend fun deleteJournyEntryById(id: Long)

    suspend fun insertJournyEntry(journyEntry: JournyEntry)
}