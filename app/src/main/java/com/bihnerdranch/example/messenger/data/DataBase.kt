package com.bihnerdranch.example.messenger.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.bihnerdranch.example.messenger.HotelContract.GuestEntry


class DataBase(context: Context) : SQLiteOpenHelper(context, "TABLE_KEY", null, 3) {
    override fun onCreate(db: SQLiteDatabase?) {
        // Обновите код для создания таблицы с новым столбцом name
        val SQL_CREATE_GUESTS_TABLE =
            ("CREATE TABLE " + GuestEntry.NAME + " ("
                    + GuestEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GuestEntry.name + " TEXT NOT NULL, "
                    + GuestEntry.primaryKey + " TEXT NOT NULL,"
                    + GuestEntry.aesKey + "TEXT NOT NULL"
                    + GuestEntry.publicKey + " TEXT NOT NULL);")
        db!!.execSQL(SQL_CREATE_GUESTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Если старая версия меньше 2, добавьте столбец name в существующую таблицу
        if (oldVersion < 2) {
            val addNameColumn = "ALTER TABLE ${GuestEntry.NAME} ADD COLUMN ${GuestEntry.name} TEXT NOT NULL DEFAULT ''"
            db!!.execSQL(addNameColumn)
        }
        if (oldVersion < 3) {
            val addAesKeyColumn = "ALTER TABLE ${GuestEntry.NAME} ADD COLUMN ${GuestEntry.aesKey} TEXT NOT NULL DEFAULT ''"
            db!!.execSQL(addAesKeyColumn)
        }
    }
}

