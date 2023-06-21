package com.bihnerdranch.example.messenger

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.*
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import javax.crypto.SecretKey


@RequiresApi(Build.VERSION_CODES.O)
class ChatActivity:AppCompatActivity(), MessageAdapter.OnItemClickListerForMessages {
    private lateinit var messageButton: Button
    private lateinit var barForMessage: EditText
    private lateinit var message: MutableList<Message>
    private lateinit var privateKey: PrivateKey
    private lateinit var publicKey: PublicKey
    private lateinit var aesKey: SecretKey
    private lateinit var testUpdateButton: Button
    private lateinit var context: Context
    private lateinit var arrayOfKeys: MutableList<String>
    private lateinit var receivedData: String
    private lateinit var sendFileButton: Button
    private lateinit var fileEncryption: FileEncryption
    private lateinit var adapter: MessageAdapter
    private lateinit var button777: Button
    lateinit var key: SecretKey
    private var id: String = "2"

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        receivedData = intent.getStringExtra("key")!!
        val sharedPreferences = getSharedPreferences("id", Context.MODE_PRIVATE)
        sharedPreferences.getString("id", null)?.let { id = it}
        setContentView(R.layout.list_of_message)
        val messagesRecycler = findViewById<RecyclerView>(R.id.messages_recycler)
        val layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(mutableListOf(), this)
        key = AesEncryption().createKey()
        fileEncryption = FileEncryption(this)
        messagesRecycler.adapter = adapter

        messagesRecycler.layoutManager = layoutManager

        message = mutableListOf<Message>()
        messageButton = findViewById(R.id.button_for_send_messages)
        barForMessage = findViewById(R.id.edit_text_for_send_messages)
        testUpdateButton = findViewById(R.id.test_button)
        sendFileButton = findViewById(R.id.send_file_button)
        button777 = findViewById(R.id.button777)

//        testButtonForTable = findViewById(R.id.button)
        context = applicationContext
        arrayOfKeys = AppealToDataBase(context).searchKeyInTable(receivedData)
        privateKey = MessageEncryption().toPrivateKey(arrayOfKeys[1])!!
        publicKey = MessageEncryption().toPublicKey(arrayOfKeys[0])!!
        aesKey = arrayOfKeys[2].stringToSecretKey()
        messageButton.setOnClickListener {
            val text = barForMessage.text.toString()
            val rightSize = text.windowed(150, 150, true)
            rightSize.forEach { adapter.messages.add(Message(it, null, Message.Position.LEFT, null)) }
            adapter.notifyDataSetChanged()
            val encryptedMessage = rightSize.map {  encryptMessage(it, publicKey)}
            runBlocking {
                launch {
                    encryptedMessage.forEach {sendMessage(it) }
                }
            }
            messagesRecycler.post {
                adapter.itemCount.takeIf { it > 0 }?.let {
                    (messagesRecycler.layoutManager as LinearLayoutManager).scrollToPosition(it - 1)
                }
            }
            barForMessage.setText("")
        }
        testUpdateButton.setOnClickListener {
            runBlocking {  launch {
                val decodingMessage = updateMessage()
                delay(500L)
                runOnUiThread {
                    decodingMessage.forEach {
                        adapter.messages.add(
                            Message(
                                it,
                                null,
                                Message.Position.RIGHT,
                                null
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
            messagesRecycler.post {
                adapter.itemCount.takeIf { it > 0 }?.let {
                    (messagesRecycler.layoutManager as LinearLayoutManager).scrollToPosition(it - 1)
                }
            }
            Log.d("q", message.toString())
//            adapter.messages.add(Message(message, Message.Position.RIGHT))
        }
        sendFileButton.setOnClickListener {
            fileEncryption.openFile()
        }
        button777.setOnClickListener {
            runBlocking {
                launch {
                    val byte = downloadFile()
                    adapter.messages.add(
                        Message(
                            "file",
                            "test",
                            Message.Position.LEFT,
                            byte

                        )
                    )
                    adapter.notifyItemInserted(adapter.messages.size + 1)
                }
            }
        }
//        messagesRecycler.addOnLayoutChangeListener { view, i, i2, i3, i4, i5, i6, i7, i8 ->
//            messagesRecycler.post {
//                adapter.itemCount.takeIf { it > 0 }?.let {
//                    (messagesRecycler.layoutManager as LinearLayoutManager).scrollToPosition(it - 1)
//                }
//            }
//        }
        (messagesRecycler.layoutManager as LinearLayoutManager).stackFromEnd = true
        val layoutParams = messagesRecycler.layoutParams

//        barForMessage.viewTreeObserver.addOnGlobalLayoutListener {
//            when (barForMessage.isKeyboardVisible()) {
//                true -> {
//                    println( getKeyboardHeight(this))
//                    layoutParams.height = height - 830
//                }
//                false -> layoutParams.height = height
//
//            }
//        }
//        val layoutParams = messagesRecycler.layoutParams
//        ViewCompat.setOnApplyWindowInsetsListener(
//            this.window.decorView
//        ) { v: View?, insets: WindowInsetsCompat ->
//            val isKeyboardVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
//            val keyboardHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
//            when (isKeyboardVisible) {
//                true -> layoutParams.height = messagesRecycler.height - keyboardHeight
//                false -> layoutParams.height = messagesRecycler.height
//
//            }
//            insets
//        }




    }
//    override fun onDestroy() {
//        barForMessage.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
//        super.onDestroy()
//    }

    override fun onItemCLickForMessages(byteArray: ByteArray) {
    Toast.makeText(this, "Item clicked: ${byteArray.toString()}", Toast.LENGTH_SHORT).show()
    try {

        val decryptedFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "kekekekekek.txt"
        )
        FileEncryption(this).decryptDataToFile(
            byteArray,
            key,
            decryptedFile
        )
    }  catch (e: InvocationTargetException) {
        e.cause?.printStackTrace()
    }

}
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == fileEncryption.OPEN_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val fileUri: Uri? = data?.data
            val encryptedData = fileUri?.let { FileEncryption(this).encryptFile(this, it, key) }
                adapter.messages.add(
                    Message(
                        encryptedData.toString(),
                        fileUri.toString(), Message.Position.RIGHT, encryptedData
                    )
                )
                adapter.notifyItemInserted(adapter.messages.size + 1)
            runBlocking {
                launch {
                    encryptedData?.let { uploadFile(it) }
                }
            }
        }
    }
    fun View.isKeyboardVisible(): Boolean { val insets = ViewCompat.getRootWindowInsets(this)
        return insets?.isVisible(WindowInsetsCompat.Type.ime()) ?: false }

    private fun setRecyclerViewHeight(recyclerView: RecyclerView, keyboardHeight: Int) {
        val layoutParams = recyclerView.layoutParams
        println(layoutParams.height)
        layoutParams.height = recyclerView.height - keyboardHeight
        recyclerView.layoutParams = layoutParams
    }

    private fun getKeyboardHeight(activity: Activity): Int? {
        val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
        return insets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_get_key -> {
                runBlocking {
                    launch {
                        val newPublicKey = getingKey(receivedData)
                        MessageEncryption().toPublicKey(newPublicKey)
                            ?.let { it1 ->
                                AppealToDataBase(context).updateValueByName(
                                    receivedData,
                                    it1
                                )
                            }
                        arrayOfKeys = AppealToDataBase(context).searchKeyInTable(receivedData)
                        privateKey = MessageEncryption().toPrivateKey(arrayOfKeys[1])!!
                        publicKey = MessageEncryption().toPublicKey(arrayOfKeys[0])!!
                    }
                    Toast.makeText(this@ChatActivity, "Ключ получен", Toast.LENGTH_SHORT).show()
                    true
                }
            }
            R.id.action_send_key -> {
                runBlocking {
                    launch {
                        sendPublicKey(arrayOfKeys[0], id)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@ChatActivity, "Ключ отправлен", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                    true
                }
            }
            R.id.action_delete -> {
                AppealToDataBase(context).deleteDB(receivedData)
                Toast.makeText(this, "Ключи удалены", Toast.LENGTH_SHORT).show()
                arrayOfKeys = AppealToDataBase(context).searchKeyInTable(receivedData)
                privateKey = MessageEncryption().toPrivateKey(arrayOfKeys[1])!!
                publicKey = MessageEncryption().toPublicKey(arrayOfKeys[0])!!
                true
            }
            R.id.action_get_AES_key -> {
                runBlocking {
                    launch {
                        val aesKey = getAesKey(id)
                        AppealToDataBase(context).updateValueByNameForAes(receivedData, aesKey)
                    }
                    true
                }
            }
            R.id.action_send_AES_key -> {
                runBlocking {
                    launch {
                        sendAesKey(aesKey.toString(), id)
                    }
                }
                true
            }
            else -> {
                Toast.makeText(this, "Вот так вот", Toast.LENGTH_SHORT).show()
                super.onOptionsItemSelected(item)
            }
        }
    }


    private suspend fun sendMessage(newMessages: ByteArray?) {
        val test = mutableListOf<Byte>()
        newMessages?.forEach { test.add(it) }
        getMessage(test, "$id$receivedData")
    }

    private suspend fun updateMessage(): List<String> {
        val notDecodeMessage = postMessage("$receivedData$id")
        println(notDecodeMessage)
        val test = notDecodeMessage.map { it
            .trim('[', ']', '\\')
            .split(", ")
            .map { str -> str.toByte() }
            .toByteArray() }.map {
                decryptMessage( it, privateKey)
        }
        Log.d("testMap", test.toString())
        return test
    }


}


