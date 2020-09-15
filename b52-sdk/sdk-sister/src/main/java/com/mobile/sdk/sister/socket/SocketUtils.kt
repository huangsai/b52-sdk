package com.mobile.sdk.sister.socket

import okio.Buffer
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.util.*

fun encrypt(byteString: ByteString): ByteString {
    val aseKey = UUID.randomUUID().toString().substring(0, 16)
    val encryptedAES = AesUtils.encrypt(byteString.toByteArray(), aseKey)
    val encryptedRSA = RSAUtils.encryptByPublicKey(aseKey.toByteArray())
    return Buffer().writeLongLe(encryptedRSA.size.toLong())
        .write(encryptedRSA)
        .writeLongLe(encryptedAES.size.toLong())
        .write(encryptedAES).readByteString()
}

fun decrypt(byteString: ByteString): ByteString {
    val buffer = Buffer().write(byteString)
    val rsaLength = buffer.readLongLe()
    val encryptedRSA = buffer.readByteArray(rsaLength)
    val aesLength = buffer.readLongLe()
    val encryptedAES = buffer.readByteArray(aesLength)
    val aseKey = RSAUtils.decryptByPrivateKey(encryptedRSA).toString(Charsets.UTF_8)
    val decryptedAES = AesUtils.decrypt(encryptedAES, aseKey)
    return decryptedAES.toByteString()
}