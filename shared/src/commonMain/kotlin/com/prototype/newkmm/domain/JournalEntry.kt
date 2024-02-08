package com.prototype.newkmm.domain

import database.JournalEntryEntity

data class JournalEntry(
    val id: Long? = 0L,
    val uuid: String = "",
    val title: String = "",
    val desc: String = "",
    val audioFile: String = "",
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)

fun JournalEntryEntity.toJournalEntry(): JournalEntry {
    return JournalEntry(
        id , uuid, title, desc, audioFile, createdAt, updatedAt
    )
}

