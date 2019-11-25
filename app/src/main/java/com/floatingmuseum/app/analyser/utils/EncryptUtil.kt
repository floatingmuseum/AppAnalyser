package com.floatingmuseum.app.analyser.utils

import java.security.MessageDigest

/**
 * Created by Floatingmuseum on 2019-11-07.
 */

const val MD5 = "MD5"
const val SHA1 = "SHA1"
const val SHA256 = "SHA256"

private val HEX_CHAR = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7',
    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
)

fun encryptToMD5(data: String?, salt: String? = null): String {
    var result = ""
    data?.let {
        result = if (salt != null) {
            encryptToMD5((it + salt).toByteArray())
        } else {
            encryptToMD5(it.toByteArray())
        }
    }
    return result
}

fun encryptToMD5(bytes: ByteArray?): String {
    var result = ""
    bytes?.let {
        val md = MessageDigest.getInstance(MD5)
        md.update(it)
        val digestedBytes = md.digest()
        result = bytesToHexString(digestedBytes)
    }
    return result
}

fun encryptToSHA1(bytes: ByteArray?): String {
    var result = ""
    bytes?.let {
        val md = MessageDigest.getInstance(SHA1)
        md.update(it)
        val digestedBytes = md.digest()
        result = bytesToHexString(digestedBytes)
    }
    return result
}

fun encryptToSHA256(bytes: ByteArray?): String {
    var result = ""
    bytes?.let {
        val md = MessageDigest.getInstance(SHA256)
        md.update(it)
        val digestedBytes = md.digest()
        result = bytesToHexString(digestedBytes)
    }
    return result
}

fun bytesToHexString(bytes: ByteArray?): String {
    if (bytes == null) return ""
    val len = bytes.size
    if (len <= 0) return ""
    val ret = CharArray(len shl 1)
    var i = 0
    var j = 0
    while (i < len) {
        ret[j++] = HEX_CHAR[bytes[i].toInt() shr 4 and 0x0f]
        ret[j++] = HEX_CHAR[bytes[i].toInt() and 0x0f]
        i++
    }
    return String(ret)
}