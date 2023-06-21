package com.bihnerdranch.example.messenger

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

class EnterIdActivity : AppCompatActivity() {
    private lateinit var buttonForAddId: Button
    private lateinit var textBar: EditText
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_id)
        buttonForAddId = findViewById(R.id.button_for_add_id)
        textBar = findViewById(R.id.input_for_id)
        buttonForAddId.setOnClickListener {
            if (textBar.text.toString() != "") {
                val sharedPreferences = getSharedPreferences("id", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("id", textBar.text.toString())
                editor.apply()
                val intent = Intent(this@EnterIdActivity, ListWithChatsActivity::class.java)
                startActivity(intent)

            }
        }
        textBar.setOnFocusChangeListener { _, hasFocus ->
            when (hasFocus) {
                true -> textBar.setText("")
                false -> textBar.setText("Enter id")
            }
        }
    }
}