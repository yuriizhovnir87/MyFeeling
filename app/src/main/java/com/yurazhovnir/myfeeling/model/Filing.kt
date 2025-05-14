package com.yurazhovnir.myfeeling.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "filing",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.NO_ACTION,
            onUpdate = ForeignKey.NO_ACTION
        )
    ]
)
data class Filing(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id", index = true)
    val id: Int,

    @ColumnInfo(name = "doneAt") val doneAt: String? = null,
    @ColumnInfo(name = "comment") val comment: String?,
    @ColumnInfo(name = "emoji") val emoji: String?,
    @ColumnInfo(name = "startsAt") val startsAt: String? = null,
    @ColumnInfo(name = "dueAt") val dueAt: String? = null,
    @ColumnInfo(name = "hydration") val hydration: Double? = null,
    @ColumnInfo(name = "steps") val steps: Int? = null,
    @ColumnInfo(name = "sleep") val sleep: Int? = null,
    @ColumnInfo(name = "userId") val userId: Int? = null
)