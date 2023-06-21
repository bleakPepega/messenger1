package com.bihnerdranch.example.messenger

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ProviderInfo
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.system.Os
import android.system.OsConstants
import android.system.StructStat
import androidx.annotation.RequiresApi
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec




class FileEncryption(private val activity: Activity) {
     val OPEN_FILE_REQUEST_CODE = 777

    fun openFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        activity.startActivityForResult(intent, OPEN_FILE_REQUEST_CODE)
    }
    fun encryptFile(context: Context, fileUri: Uri, aesKey: SecretKey): ByteArray {
        val inputStream = context.contentResolver.openInputStream(fileUri)
        val outputStream = ByteArrayOutputStream()

        // Генерируем IV
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        val ivSpec = IvParameterSpec(iv)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING").apply {
            init(Cipher.ENCRYPT_MODE, aesKey, ivSpec)
        }
        val cipherInputStream = CipherInputStream(inputStream, cipher)

        // Сначала записываем IV в outputStream
        outputStream.write(iv)

        // Затем копируем зашифрованные данные в outputStream
        cipherInputStream.copyTo(outputStream)

        val encryptedData = outputStream.toByteArray()

        cipherInputStream.close()
        outputStream.close()
        inputStream?.close()

        return encryptedData
    }

    fun decryptDataToFile(encryptedData: ByteArray, aesKey: SecretKey, decryptedFile: File) {
        // Извлекаем IV из данных
        val iv = encryptedData.copyOfRange(0, 16)
        val ivSpec = IvParameterSpec(iv)

        val cipherData = encryptedData.copyOfRange(16, encryptedData.size)

        val inputStream = ByteArrayInputStream(cipherData)
        val outputStream = FileOutputStream(decryptedFile)

        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING").apply {
            init(Cipher.DECRYPT_MODE, aesKey, ivSpec)
        }
        val cipherOutputStream = CipherOutputStream(outputStream, cipher)

        inputStream.copyTo(cipherOutputStream)

        cipherOutputStream.close()
        outputStream.close()
        inputStream.close()
    }



    fun belongsToCurrentApplication(ctx: Context, uri: Uri): Boolean {
        val authority: String = uri.authority.toString()
        val info: ProviderInfo =
            ctx.packageManager.resolveContentProvider(authority, 0)!!

        return ctx.packageName.equals(info.packageName)
    }
    fun isExported(ctx: Context, uri: Uri): Boolean {
        val authority = uri.authority.toString()
        val info: ProviderInfo =
            ctx.packageManager.resolveContentProvider(authority, 0)!!

        return info.exported
    }

}
fun Uri.isValidFile(ctx: Context, pfd: ParcelFileDescriptor): Boolean {
    // Canonicalize to resolve symlinks and path traversals.
    val fdCanonical = File(this.path!!).canonicalPath

    val pfdStat: StructStat = Os.fstat(pfd.fileDescriptor)


    // Lstat doesn't follow the symlink.
    val canonicalFileStat: StructStat = Os.lstat(fdCanonical)

    // Since we canonicalized (followed the links) the path already,
    // the path shouldn't point to symlink unless it was changed in the
    // meantime.
    if (OsConstants.S_ISLNK(canonicalFileStat.st_mode)) {
        return false
    }

    val sameFile =
        pfdStat.st_dev == canonicalFileStat.st_dev &&
                pfdStat.st_ino == canonicalFileStat.st_ino

    if (!sameFile) {
        return false
    }

    return !isBlockedPath(ctx, fdCanonical)
}
fun isBlockedPath(ctx: Context, fdCanonical: String): Boolean {
    // Paths that should rarely be exposed
    if (fdCanonical.startsWith("/proc/") ||
        fdCanonical.startsWith("/data/misc/")) {
        return true
    }
    else return false

    // Implement logic to block desired directories. For example, specify
    // the entire app data/ directory to block all access.
}

@RequiresApi(Build.VERSION_CODES.O)
fun String.stringToSecretKey(): SecretKey {
    val decodedKey: ByteArray = Base64.getDecoder().decode(this)
    return SecretKeySpec(decodedKey, 0, decodedKey.size, "AES")
}