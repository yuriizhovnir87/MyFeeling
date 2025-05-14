package com.yurazhovnir.myfeeling.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    val name: String?,
    val email: String?,
    val password: String?,
    val subscribe: String?,
    val startsReminderId: Int?,
    val dueAt: String?
)
