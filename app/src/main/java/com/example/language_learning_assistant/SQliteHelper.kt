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
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "words.db"
        private const val TBL_WORDS = "tbl_words"
        private const val ID = "id"
        private const val ARTICLE = "article"
        private const val NAME = "name"
        private const val MEANING = "meaning"
        private const val PLURAL = "plural"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblWords = ("CREATE TABLE $TBL_WORDS " +
                "($ID INTEGER PRIMARY KEY, $ARTICLE TEXT NOT NULL, $NAME TEXT NOT NULL, $MEANING TEXT NOT NULL, $PLURAL TEXT NOT NULL)")
        db?.execSQL(createTblWords)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TBL_WORDS")
        onCreate(db)
    }

    fun insertWords(std: WordModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(ARTICLE, std.article)
        contentValues.put(NAME, std.name)
        contentValues.put(MEANING, std.meaning)
        contentValues.put(PLURAL, std.plural)
        val success = db.insert(TBL_WORDS, null, contentValues)
        db.close()
        return success
    }

    @SuppressLint("Range")
    fun getAllWords(): ArrayList<WordModel> {
        val wrdList: ArrayList<WordModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_WORDS"
        val db = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }
        var id: Int
        var article: String
        var name: String
        var meaning: String
        var plural: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(ID))
                article = cursor.getString(cursor.getColumnIndex(ARTICLE))
                name = cursor.getString(cursor.getColumnIndex(NAME))
                meaning = cursor.getString(cursor.getColumnIndex(MEANING))
                plural = cursor.getString(cursor.getColumnIndex(PLURAL))

                val std = WordModel(id = id, article = article, name = name, meaning = meaning, plural = plural)
                wrdList.add(std)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return wrdList
    }

    fun updateWords(wrd: WordModel): Int {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ARTICLE, wrd.article)
        contentValues.put(NAME, wrd.name)
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
