package com.example.language_learning_assistant

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "words.db"
        private const val TBL_WORDS = "tbl_words"
        private const val ID = "id"
        private const val ARTICLE = "article"
        private const val NAME = "name"
        private const val MEANING = "meaning"
        private const val PLURAL = "plural"
        private const val PART_OF_SPEECH = "part_of_speech"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblWords = ("CREATE TABLE $TBL_WORDS " +
                "($ID INTEGER PRIMARY KEY, " +
                "$ARTICLE TEXT NOT NULL, " +
                "$NAME TEXT NOT NULL, " +
                "$PART_OF_SPEECH TEXT NOT NULL, " +
                "$MEANING TEXT NOT NULL, " +
                "$PLURAL TEXT NOT NULL)")
        db?.execSQL(createTblWords)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db?.execSQL("ALTER TABLE $TBL_WORDS ADD COLUMN $PART_OF_SPEECH TEXT NOT NULL DEFAULT ''")
        }
    }

    // Insert a new word
    fun insertWords(wrd: WordModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(ARTICLE, wrd.article)
            put(NAME, wrd.name)
            put(PART_OF_SPEECH, wrd.part_of_speech)
            put(MEANING, wrd.meaning)
            put(PLURAL, wrd.plural)
        }
        val success = db.insert(TBL_WORDS, null, contentValues)
        db.close()
        return success
    }

    // Retrieve all words
    @SuppressLint("Range")
    fun getAllWords(): ArrayList<WordModel> {
        val wordList: ArrayList<WordModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_WORDS"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            return ArrayList()
        }

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(ID))
                val article = cursor.getString(cursor.getColumnIndex(ARTICLE))
                val name = cursor.getString(cursor.getColumnIndex(NAME))
                val partOfSpeech = cursor.getString(cursor.getColumnIndex(PART_OF_SPEECH))
                val meaning = cursor.getString(cursor.getColumnIndex(MEANING))
                val plural = cursor.getString(cursor.getColumnIndex(PLURAL))

                val word = WordModel(id, article, name, partOfSpeech, meaning, plural)
                wordList.add(word)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return wordList
    }

    // Update an existing word
    fun updateWord(wrd: WordModel): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(ARTICLE, wrd.article)
            put(NAME, wrd.name)
            put(PART_OF_SPEECH, wrd.part_of_speech)
            put(MEANING, wrd.meaning)
            put(PLURAL, wrd.plural)
        }
        val success = db.update(TBL_WORDS, contentValues, "$ID=?", arrayOf(wrd.id.toString()))
        db.close()
        return success
    }

    // Delete a word by its ID
    fun deleteWordsByID(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TBL_WORDS, "$ID=?", arrayOf(id.toString()))
        db.close()
        return success
    }
}
