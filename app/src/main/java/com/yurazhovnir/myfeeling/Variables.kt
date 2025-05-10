package com.yurazhovnir.myfeeling

import android.content.res.Resources
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HydrationRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord

val PERMISSIONS = setOf(
    HealthPermission.getReadPermission(StepsRecord::class),
    HealthPermission.getReadPermission(HydrationRecord::class),
    HealthPermission.getReadPermission(SleepSessionRecord::class),
)
enum class FeelingType(val emoji: String, val label: String) {
    HAPPY("😄", "Happy"),
    SMILE("😊", "Content"),
    NEUTRAL("😐", "Neutral"),
    SAD("😢", "Sad"),
    ANGRY("😡", "Angry");

    companion object {
        fun fromEmoji(emoji: String): FeelingType? {
            return values().find { it.emoji == emoji }
        }
    }
}
val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
