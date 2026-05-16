package com.sanaos.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SanaDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS contacts_memory (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                phone TEXT,
                platform TEXT,
                priority INTEGER DEFAULT 50
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS interaction_history (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp INTEGER,
                user_input TEXT,
                sana_response TEXT,
                category TEXT
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS reminders_table (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                label TEXT,
                trigger_ms INTEGER,
                fired INTEGER DEFAULT 0
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE contacts_memory ADD COLUMN priority INTEGER DEFAULT 50")
        }

        if (oldVersion < 3) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS reminders_table (
                    _id INTEGER PRIMARY KEY AUTOINCREMENT,
                    label TEXT,
                    trigger_ms INTEGER,
                    fired INTEGER DEFAULT 0
                )
                """.trimIndent()
            )
        }
    }

    companion object {
        private const val DB_NAME = "sana_memory_db"
        private const val DB_VERSION = 3
    }
}
