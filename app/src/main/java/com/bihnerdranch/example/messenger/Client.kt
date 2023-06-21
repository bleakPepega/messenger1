package com.bihnerdranch.example.messenger
import android.net.Uri
import android.os.Environment
import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.CIO
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import java.io.File

@OptIn(DelicateCoroutinesApi::class)
suspend fun serverHost(): ByteArray? {
    var test = ""
    GlobalScope.launch {
        val socket = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp()
            .connect(InetSocketAddress("185.242.107.62", 4567))
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)
        output.writeFully("/messages".toByteArray())
        val readyInput = input.readUTF8Line()
        test = readyInput.toString()
        Log.d("testTime", "$test in cor")
        withContext(Dispatchers.IO) {
            socket.close()
        }
    }
    delay(500L)
    Log.d("testTime", "$test well")
    return test.toByteArray()
}
@OptIn(InternalAPI::class)
suspend fun getMessage(combination: MutableList<Byte>, id: String) {
    val json = """
        {
            "message": "$combination"
        }
    """
    val client = HttpClient()
    val response = client.post("http://185.242.107.62:4567/messages/$id") {
        body = json
        contentType(ContentType.Application.Json)
    }
    Log.d("Server Response", "${response.status}")
}
suspend fun postMessage(id: String): List<String> {
    val client = HttpClient()
    val answer = client.get("http://185.242.107.62:4567/messages/$id").body<String>().let { it.substring(3, it.length - 3) }.replace("\\", "")
    val test = answer.split("\",\"")
    return test

}
suspend fun getingKey(id: String): String {
    val client = HttpClient()
    val answer = client.get("http://185.242.107.62:4567/publicKey/$id").body<String>().let { it.substring(2, it.length - 2) }
    return answer
}
@OptIn(InternalAPI::class)
suspend fun sendPublicKey(key: String, id: String) {
    val json = """
        {
            "message": "$key"
        }
    """
    val client = HttpClient()
    val response = client.post("http://185.242.107.62:4567/publicKey/$id") {
        body = json
        contentType(ContentType.Application.Json)
    }
    Log.d("Server Response", "${response.status}")
}
suspend fun uploadFile(byteArray: ByteArray) {
    val client = HttpClient(CIO)

    val response: HttpResponse = client.submitFormWithBinaryData(
        url = "http://185.242.107.62:4567/upload/kekega",
        formData = formData {
            append("description", "Some image description")
            append(
                "file",
                byteArray,
                Headers.build {
                    append(HttpHeaders.ContentType, "text/plain")
                    append(HttpHeaders.ContentDisposition, "filename=\"file.txt\"")
                }
            )
        }
    )
    client.close()
}
suspend fun downloadFile(): ByteArray {
    val client = HttpClient(CIO)

    val url = "http://185.242.107.62:4567/download/kekega" // URL файла для загрузки
    val response: HttpResponse = client.get(url)

    val bytes = response.readBytes() // чтение байтов ответа
//
//    val file = File(
//        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//        "decryptedFile2.txt"
//    )
//    file.writeBytes(bytes) // запись байтов в файл
    client.close()
    return bytes
}
suspend fun getAesKey(id: String): String {
    val client = HttpClient()
    val answer = client.get("http://185.242.107.62:4567/getPublickKey/$id").body<String>().let { it.substring(2, it.length - 2) }
    return answer
}
@OptIn(InternalAPI::class)
suspend fun sendAesKey(key: String, id: String) {
    val json = """
        {
            "message": "$key"
        }
    """
    val client = HttpClient()
    val response = client.post("http://185.242.107.62:4567/senAesKey/$id") {
        body = json
        contentType(ContentType.Application.Json)
    }
    Log.d("Server Response", "${response.status}")
}