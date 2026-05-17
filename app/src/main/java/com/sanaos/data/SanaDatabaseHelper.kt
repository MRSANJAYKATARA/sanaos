package com.sanaos.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

data class HistoryItem(
    val id: Long,
    val timestamp: Long,
    val userInput: String,
    val sanaResponse: String,
    val category: String
)

class SanaDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        const val DATABASE_NAME = "sana_memory_db"
        const val DATABASE_VERSION = 3

        // Contacts Memory Table
        const val TABLE_CONTACTS = "contacts_memory"
        const val COL_CONTACT_ID = "_id"
        const val COL_CONTACT_NAME = "name"
        const val COL_CONTACT_PHONE = "phone"
        const val COL_CONTACT_PLATFORM = "platform"
        const val COL_CONTACT_PRIORITY = "priority"

        // Interaction History Table
        const val TABLE_HISTORY = "interaction_history"
        const val COL_HISTORY_ID = "_id"
        const val COL_HISTORY_TIMESTAMP = "timestamp"
        const val COL_HISTORY_USER_INPUT = "user_input"
        const val COL_HISTORY_SANA_RESPONSE = "sana_response"
        const val COL_HISTORY_CATEGORY = "category"

        // Reminders Table
        const val TABLE_REMINDERS = "reminders_table"
        const val COL_REMINDER_ID = "_id"
        const val COL_REMINDER_LABEL = "label"
        const val COL_REMINDER_TRIGGER_MS = "trigger_ms"
        const val COL_REMINDER_FIRED = "fired"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create contacts_memory table
        db.execSQL(
            """CREATE TABLE $TABLE_CONTACTS (
                $COL_CONTACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CONTACT_NAME TEXT NOT NULL,
                $COL_CONTACT_PHONE TEXT,
                $COL_CONTACT_PLATFORM TEXT DEFAULT 'call',
                $COL_CONTACT_PRIORITY INTEGER DEFAULT 50
            )"""
        )

        // Create interaction_history table
        db.execSQL(
            """CREATE TABLE $TABLE_HISTORY (
                $COL_HISTORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_HISTORY_TIMESTAMP INTEGER,
                $COL_HISTORY_USER_INPUT TEXT NOT NULL,
                $COL_HISTORY_SANA_RESPONSE TEXT NOT NULL,
                $COL_HISTORY_CATEGORY TEXT DEFAULT 'CHAT'
            )"""
        )

        // Create reminders_table
        db.execSQL(
            """CREATE TABLE $TABLE_REMINDERS (
                $COL_REMINDER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_REMINDER_LABEL TEXT,
                $COL_REMINDER_TRIGGER_MS INTEGER,
                $COL_REMINDER_FIRED INTEGER DEFAULT 0
            )"""
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            // Drop and recreate tables for now (in production, use migrations)
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_HISTORY")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_REMINDERS")
            onCreate(db)
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // HISTORY METHODS (BUG-11 FIX)
    // ═══════════════════════════════════════════════════════════════

    fun insertHistory(userInput: String, sanaResponse: String, category: String = "CHAT") {
        try {
            val db = writableDatabase
            val cv = android.content.ContentValues().apply {
                put(COL_HISTORY_TIMESTAMP, System.currentTimeMillis())
                put(COL_HISTORY_USER_INPUT, userInput)
                put(COL_HISTORY_SANA_RESPONSE, sanaResponse)
                put(COL_HISTORY_CATEGORY, category)
            }
            db.insert(TABLE_HISTORY, null, cv)
        } catch (e: Exception) {
            Log.e("SANA_DB", "Error inserting history: ${e.message}", e)
        }
    }

    fun getAllHistory(): List<HistoryItem> {
        return try {
            val db = readableDatabase
            val cursor = db.query(
                TABLE_HISTORY,
                null,
                null,
                null,
                null,
                null,
                "$COL_HISTORY_ID DESC"
            )
            val items = mutableListOf<HistoryItem>()
            while (cursor.moveToNext()) {
                items.add(
                    HistoryItem(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_HISTORY_ID)),
                        timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COL_HISTORY_TIMESTAMP)),
                        userInput = cursor.getString(cursor.getColumnIndexOrThrow(COL_HISTORY_USER_INPUT)),
                        sanaResponse = cursor.getString(cursor.getColumnIndexOrThrow(COL_HISTORY_SANA_RESPONSE)),
                        category = cursor.getString(cursor.getColumnIndexOrThrow(COL_HISTORY_CATEGORY))
                    )
                )
            }
            cursor.close()
            items
        } catch (e: Exception) {
            Log.e("SANA_DB", "Error getting history: ${e.message}", e)
            emptyList()
        }
    }

    fun trimHistoryToLimit(limit: Int) {
        try {
            val db = writableDatabase
            db.execSQL(
                """DELETE FROM $TABLE_HISTORY WHERE $COL_HISTORY_ID NOT IN
                    (SELECT $COL_HISTORY_ID FROM $TABLE_HISTORY ORDER BY $COL_HISTORY_ID DESC LIMIT ?)""",
                arrayOf(limit)
            )
        } catch (e: Exception) {
            Log.e("SANA_DB", "Error trimming history: ${e.message}", e)
        }
    }
}
