package com.yurazhovnir.myfeeling.helper

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.yurazhovnir.myfeeling.model.Filing


class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val listeners = mutableListOf<DatabaseChangeListener>()

    // Додавання слухача
    fun addDatabaseChangeListener(listener: DatabaseChangeListener) {
        listeners.add(listener)
       val test =  listeners
        test
    }

    // Видалення слухача
    fun removeDatabaseChangeListener(listener: DatabaseChangeListener) {
        listeners.remove(listener)
    }

    // Виклик всіх слухачів
    private fun notifyDataChanged() {
        listeners.forEach { it.onDataChanged() }
    }
    companion object {
        const val DATABASE_NAME = "my_feeling.db"
        const val DATABASE_VERSION = 1

        const val TABLE_USER = "user"
        const val USER_ID = "id"
        const val USER_NAME = "name"
        const val USER_EMAIL = "email"
        const val USER_PASSWORD = "password"
        const val USER_SUBSCRIBE = "subscribe"
        const val USER_STARTS_REMINDER_ID = "startsReminderId"
        const val USER_DUE_AT = "dueAt"

        const val TABLE_FILING = "filing"
        const val FILING_ID = "id"
        const val FILING_DONE_AT = "doneAt"
        const val FILING_COMMENT = "comment"
        const val FILING_EMOJI = "emoji"
        const val FILING_STARTS_AT = "startsAt"
        const val FILING_DUE_AT = "dueAt"
        const val FILING_HYDRATION = "hydration"
        const val FILING_STEPS = "steps"
        const val FILING_SLEEP = "sleep"
        const val FILING_USER_ID = "userId"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
            CREATE TABLE $TABLE_USER (
                $USER_ID INTEGER PRIMARY KEY,
                $USER_NAME TEXT,
                $USER_EMAIL TEXT,
                $USER_PASSWORD TEXT,
                $USER_SUBSCRIBE TEXT,
                $USER_STARTS_REMINDER_ID INTEGER,
                $USER_DUE_AT TEXT
            );
        """.trimIndent()

        val createFilingTable = """
            CREATE TABLE $TABLE_FILING (
                $FILING_ID INTEGER PRIMARY KEY,
                $FILING_DONE_AT TEXT,
                $FILING_COMMENT TEXT,
                $FILING_EMOJI TEXT,
                $FILING_STARTS_AT TEXT,
                $FILING_DUE_AT TEXT,
                $FILING_HYDRATION REAL,
                $FILING_STEPS INTEGER,
                $FILING_SLEEP INTEGER,
                $FILING_USER_ID INTEGER,
                FOREIGN KEY($FILING_USER_ID) REFERENCES $TABLE_USER($USER_ID)
            );
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createFilingTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FILING")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    fun insertFiling(filing: Filing) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(FILING_ID, filing.id)
            put(FILING_DONE_AT, filing.doneAt)
            put(FILING_COMMENT, filing.comment)
            put(FILING_EMOJI, filing.emoji)
            put(FILING_STARTS_AT, filing.startsAt)
            put(FILING_DUE_AT, filing.dueAt)
            put(FILING_HYDRATION, filing.hydration)
            put(FILING_STEPS, filing.steps)
            put(FILING_SLEEP, filing.sleep)
            put(FILING_USER_ID, filing.userId)  // Переконайся, що це значення є у Filing
        }
        db.insert(TABLE_FILING, null, values)
        notifyDataChanged()
    }

    @SuppressLint("Range")
    fun getAllFilings(): List<Filing> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_FILING, // Назва таблиці
            null, // Всі стовпці
            null, // Без фільтра
            null, // Без аргументів для фільтра
            null, // Без групування
            null, // Без сортування
            null // Без порядку сортування
        )

        val filings = mutableListOf<Filing>()
        with(cursor) {
            while (moveToNext()) {
                val filing = Filing(
                    id = getInt(getColumnIndex(FILING_ID)),
                    doneAt = getString(getColumnIndex(FILING_DONE_AT)),
                    comment = getString(getColumnIndex(FILING_COMMENT)),
                    emoji = getString(getColumnIndex(FILING_EMOJI)),
                    startsAt = getString(getColumnIndex(FILING_STARTS_AT)),
                    dueAt = getString(getColumnIndex(FILING_DUE_AT)),
                    hydration = getDouble(getColumnIndex(FILING_HYDRATION)),
                    steps = getInt(getColumnIndex(FILING_STEPS)),
                    sleep = getInt(getColumnIndex(FILING_SLEEP)),
                    userId = getInt(getColumnIndex(FILING_USER_ID)) // Для userId
                )
                filings.add(filing)
            }
        }
        cursor.close()
        return filings
    }
}
interface DatabaseChangeListener {
    fun onDataChanged()
}
