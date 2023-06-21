package com.bihnerdranch.example.messenger

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPreferences = getSharedPreferences("id", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        if (sharedPreferences.contains("id")) {
            sharedPreferences.getString("id", null)?.let { Log.d("testId", it) }
            val intent = Intent(this@MainActivity, ListWithChatsActivity::class.java)
            startActivity(intent)
        }
        else {
            val intent = Intent(this@MainActivity, EnterIdActivity::class.java)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun nextActivity(view: View) {
        val intent = Intent(this@MainActivity, ChatActivity::class.java)
        startActivity(intent)

    }




}