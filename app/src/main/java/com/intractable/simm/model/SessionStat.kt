package com.intractable.simm.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session_table")
data class SessionStat(@PrimaryKey @ColumnInfo(name = "sessionStat")val sessionStat: String
)
