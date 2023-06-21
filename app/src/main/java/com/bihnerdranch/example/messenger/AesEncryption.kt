package com.bihnerdranch.example.messenger
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class AesEncryption {
    private val keyGenerator: KeyGenerator = KeyGenerator.getInstance("AES")
    init {
        keyGenerator.init(256)
    }
    fun createKey(): SecretKey = keyGenerator.generateKey()

}
