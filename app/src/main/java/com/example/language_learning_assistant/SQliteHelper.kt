package com.example.language_learning_assistant

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.lang.Exception

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


    fun insertWords(std: WordModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ARTICLE, std.article)
        contentValues.put(NAME, std.name)
        contentValues.put(PART_OF_SPEECH, std.part_of_speech)
        contentValues.put(MEANING, std.meaning)
        contentValues.put(PLURAL, std.plural)
        val success = db.insert(TBL_WORDS, null, contentValues)
        db.close()
        return success
    }

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
                val idIndex = cursor.getColumnIndex(ID)
                val articleIndex = cursor.getColumnIndex(ARTICLE)
                val nameIndex = cursor.getColumnIndex(NAME)
                val partOfSpeechIndex = cursor.getColumnIndex(PART_OF_SPEECH)
                val meaningIndex = cursor.getColumnIndex(MEANING)
                val pluralIndex = cursor.getColumnIndex(PLURAL)

                if (idIndex != -1 && articleIndex != -1 && nameIndex != -1 && partOfSpeechIndex != -1 && meaningIndex != -1 && pluralIndex != -1) {
                    val id = cursor.getInt(idIndex)
                    val article = cursor.getString(articleIndex)
                    val name = cursor.getString(nameIndex)
                    val partOfSpeech = cursor.getString(partOfSpeechIndex)
                    val meaning = cursor.getString(meaningIndex)
                    val plural = cursor.getString(pluralIndex)

                    val word = WordModel(id, article, name, partOfSpeech, meaning, plural)
                    wordList.add(word)
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return wordList
    }


    fun updateWords(wrd: WordModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ARTICLE, wrd.article)
        contentValues.put(NAME, wrd.name)
        contentValues.put(PART_OF_SPEECH, wrd.part_of_speech)
        contentValues.put(MEANING, wrd.meaning)
        contentValues.put(PLURAL, wrd.plural)


        val success = db.update(TBL_WORDS, contentValues, "$ID=?", arrayOf(wrd.id.toString()))
        db.close()
        return success
    }

    fun deleteWordsByID(id: Int): Int {
        val db = this.writableDatabase
        val success = db.delete(TBL_WORDS, "$ID=?", arrayOf(id.toString()))
        db.close()
        return success
    }
}
