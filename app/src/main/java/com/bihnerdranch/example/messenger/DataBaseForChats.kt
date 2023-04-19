package com.bihnerdranch.example.messenger

import android.content.ContentValues
import android.content.Context
import com.bihnerdranch.example.messenger.data.DbForChats
import com.bihnerdranch.example.messenger.data.TableForChat

class DataBaseForChats(val context: Context) {
    fun insertValue(id: String) {
        val dataBase = DbForChats(context).writableDatabase
        val value = ContentValues().apply {
            put(TableForChat.Chat.NAME, id)
        }
        val newRowId = dataBase.insert(TableForChat.Chat.NAME, null, value)

    }
     fun readValue(): MutableList<String> {
        val id1 = mutableListOf<String>()
        val dataBase = DbForChats(context).readableDatabase
         val projection = arrayOf(TableForChat.Chat.ID, TableForChat.Chat.NAME)
//        val selection = "${TableForChat.Chat.NAME} = ?"
//        val selectionArgs = arrayOf(name)
        val cursor = dataBase.query(
            TableForChat.Chat.NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    id1 += it.getString(it.getColumnIndexOrThrow(TableForChat.Chat.NAME))
                } while (it.moveToNext())
            }
        }
        return id1
    }
    fun deleteValue (name: String) {
        val dataBase = DbForChats(context).writableDatabase
        val selection = "${TableForChat.Chat.NAME} = ?"
        val selectionArgs = arrayOf(name)
        dataBase.delete(TableForChat.Chat.NAME, selection, selectionArgs)
    }
}