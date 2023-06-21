package com.bihnerdranch.example.messenger

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.bihnerdranch.example.messenger.data.DataBase
import java.security.PublicKey
import java.util.Base64
import javax.crypto.KeyGenerator


class AppealToDataBase(val context: Context) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun insertValue(name: String) {
        val database = DataBase(context).writableDatabase
        val messageEncryption = MessageEncryption()
        val privateKey = messageEncryption.privateKey
        val publicKey = messageEncryption.publicKey
        val values = ContentValues().apply {
            put(HotelContract.GuestEntry.name, name)
            put(HotelContract.GuestEntry.primaryKey, "${messageEncryption.keyToString(privateKey)}")
            put(HotelContract.GuestEntry.publicKey, "${messageEncryption.keyToString(publicKey)}")
            put(HotelContract.GuestEntry.aesKey, "${AesEncryption().createKey()}")
        }
        database.insert(HotelContract.GuestEntry.NAME, null, values)
    }

    private fun readValue(name: String): MutableList<String> {
        var primaryKeyString = ""
        var publicKeyString = ""
        var  aesKey = ""
        val database = DataBase(context).readableDatabase
        val projection = arrayOf(HotelContract.GuestEntry.ID, HotelContract.GuestEntry.primaryKey, HotelContract.GuestEntry.publicKey)

        // Добавьте параметры выборки и аргументы выборки
        val selection = "${HotelContract.GuestEntry.name} = ?"
        val selectionArgs = arrayOf(name)

        val cursor = database.query(
            HotelContract.GuestEntry.NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                do {
                    val id = it.getInt(it.getColumnIndexOrThrow(HotelContract.GuestEntry.ID))
                    primaryKeyString = it.getString(it.getColumnIndexOrThrow(HotelContract.GuestEntry.primaryKey))
                    publicKeyString = it.getString(it.getColumnIndexOrThrow(HotelContract.GuestEntry.publicKey))
                    aesKey = it.getString(it.getColumnIndexOrThrow(HotelContract.GuestEntry.aesKey))
                    Log.d("TEST TABLE", "$id, $primaryKeyString, $publicKeyString")
                } while (it.moveToNext())
            }
        }
        return mutableListOf(publicKeyString, primaryKeyString)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun searchKeyInTable(name: String): MutableList<String> {
        val listWithKey = readValue(name)
        println(listWithKey[0])
        return if (listWithKey[0] != "" && listWithKey[1] != "") listWithKey
        else {
            insertValue(name)
            readValue(name)
        }
    }

    fun deleteDB(name: String) {
        val database = DataBase(context).writableDatabase
        val selection = "${HotelContract.GuestEntry.name}=?"
        val selectionArgs = arrayOf(name)

        database.delete(HotelContract.GuestEntry.NAME, selection, selectionArgs)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateValueByName(name: String, publicKey: PublicKey) {
        val database = DataBase(context).writableDatabase
        val messageEncryption = MessageEncryption()
        Log.d("public", publicKey.toString())

        val values = ContentValues().apply {
            put(HotelContract.GuestEntry.publicKey, "${messageEncryption.keyToString(publicKey)}")
        }

        // Используем имя вместо ID для выборки
        val selection = "${HotelContract.GuestEntry.name} = ?"
        val selectionArgs = arrayOf(name)

        database.update(HotelContract.GuestEntry.NAME, values, selection, selectionArgs)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateValueByNameForAes(name: String, AesKey: String) {
        val database = DataBase(context).writableDatabase
        val encodedKey: String = Base64.getEncoder().encodeToString(AesKey.toByteArray())
        val values = ContentValues().apply {
            put(HotelContract.GuestEntry.aesKey, AesKey)
        }

        // Используем имя вместо ID для выборки
        val selection = "${HotelContract.GuestEntry.name} = ?"
        val selectionArgs = arrayOf(name)

        database.update(HotelContract.GuestEntry.NAME, values, selection, selectionArgs)
    }

    @SuppressLint("Range")
    fun getFirstID(): Int {
        val database = DataBase(context).readableDatabase

        // Создаем SQL-запрос для выборки записей с сортировкой по ID
        val selectQuery = "SELECT * FROM ${HotelContract.GuestEntry.NAME} ORDER BY ${HotelContract.GuestEntry.ID} ASC LIMIT 1"
        val cursor = database.rawQuery(selectQuery, null)

        // Получаем первое значение ID, если оно существует
        val firstID = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndex(HotelContract.GuestEntry.ID))
        } else {
            1
        }

        cursor.close()
        println(firstID)
        return firstID
    }

}