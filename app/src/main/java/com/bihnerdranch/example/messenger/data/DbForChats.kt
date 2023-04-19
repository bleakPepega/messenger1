package com.bihnerdranch.example.messenger.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.bihnerdranch.example.messenger.HotelContract

class DbForChats(context: Context) : SQLiteOpenHelper(context, "Table", null, 1) {
    override fun onCreate(p0: SQLiteDatabase?) {
        val SQL_CREATE_CHATS_TABLE =
            ("CREATE TABLE " + TableForChat.Chat.NAME + " (" +
                    TableForChat.Chat.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableForChat.Chat.NAME + " TEXT NOT NULL); ")
        p0!!.execSQL(SQL_CREATE_CHATS_TABLE)
    }


    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}