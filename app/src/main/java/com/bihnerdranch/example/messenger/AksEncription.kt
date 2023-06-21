//package com.bihnerdranch.example.messenger
//
//import android.content.Context
//import android.os.Build
//import android.security.keystore.KeyGenParameterSpec
//import android.security.keystore.KeyProperties
//import androidx.annotation.RequiresApi
//import java.security.KeyStore
//import javax.crypto.Cipher
//import javax.crypto.KeyGenerator
//import javax.crypto.SecretKey
//import javax.crypto.spec.GCMParameterSpec
//
//class AksEncription {
//    @RequiresApi(Build.VERSION_CODES.M)
//    fun createKey(alias: String): SecretKey {
//        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
//
//        val keyGenParameterSpec = KeyGenParameterSpec.Builder(alias,
//            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
//            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
//            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
//            .setRandomizedEncryptionRequired(false) // использование идентичного nonce для возможности дешифрования
//            .build()
//
//        keyGenerator.init(keyGenParameterSpec)
//
//        return keyGenerator.generateKey()
//    }
//
//    // 2. Шифрование существующего AES-ключа
//    fun encryptKey(alias: String, keyToEncrypt: ByteArray): ByteArray {
//        val keyStore = KeyStore.getInstance("AndroidKeyStore")
//        keyStore.load(null)
//
//        val secretKey = keyStore.getKey(alias, null) as SecretKey
//        val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}")
//
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, ByteArray(12))) // пустой nonce
//        return cipher.doFinal(keyToEncrypt)
//    }
//
//    // 3. Сохранение зашифрованного ключа
//    fun saveEncryptedKey(context: Context, alias: String, encryptedKey: ByteArray) {
//        context.getSharedPreferences("key_prefs", Context.MODE_PRIVATE).edit()
//            .putString(alias, Base64.encodeToString(encryptedKey, Base64.DEFAULT))
//            .apply()
//    }
//    fun getKeyFromKeystore(alias: String): SecretKey {
//        val keyStore = KeyStore.getInstance("AndroidKeyStore")
//        keyStore.load(null)
//
//        return keyStore.getKey(alias, null) as SecretKey
//    }
//
//    // 2. Извлечение зашифрованного ключа из SharedPreferences
//    fun getEncryptedKey(context: Context, alias: String): ByteArray {
//        val encryptedKeyBase64 = context.getSharedPreferences("key_prefs", Context.MODE_PRIVATE)
//            .getString(alias, null)
//
//        return Base64.decode(encryptedKeyBase64, Base64.DEFAULT)
//    }
//
//    // 3. Расшифровка зашифрованного ключа
//    @RequiresApi(Build.VERSION_CODES.M)
//    fun decryptKey(alias: String, encryptedKey: ByteArray): ByteArray {
//        val secretKey = getKeyFromKeystore(alias)
//        val cipher = Cipher.getInstance("${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_GCM}/${KeyProperties.ENCRYPTION_PADDING_NONE}")
//
//        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, ByteArray(12))) // пустой nonce
//        return cipher.doFinal(encryptedKey)
//    }
//}