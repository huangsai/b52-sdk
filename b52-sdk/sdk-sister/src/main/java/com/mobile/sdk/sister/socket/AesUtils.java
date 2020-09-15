package com.mobile.sdk.sister.socket;

import androidx.annotation.NonNull;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AesUtils {

    @NonNull
    public static byte[] encrypt(@NonNull byte[] decryptedBytes, @NonNull String sKey) throws Exception {
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            throw new IllegalStateException();
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(raw);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(decryptedBytes);
        return encrypted;
    }

    @NonNull
    public static byte[] decrypt(@NonNull byte[] encryptedBytes, @NonNull String sKey) throws Exception {
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            throw new IllegalStateException();
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(raw);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");// "算法/模式/补码方式"
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return decryptedBytes;
    }
}
