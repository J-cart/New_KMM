package com.prototype.newkmm.domain

import database.JournalEntryEntity

data class JournyEntry(
    val id: Long? = 0L,
    val uuid: String = "",
    val title: String = "",
    val desc: String = "",
    val audioFile: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)

fun JournalEntryEntity.toJournalEntry(): JournyEntry {
    return JournyEntry(
        id , uuid, title, desc, audioFile, createdAt, updatedAt
    )
}

