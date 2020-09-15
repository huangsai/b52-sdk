package com.mobile.sdk.sister.socket;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

public class RSAUtils {

    private static final String publicExponent = "65537";
    private static final String privateExponent = "7607163343763658129447301273565035242203345761158462583533166995124297775681233378590981895063556481816278052788936602721832980826455900406610280113354241";
    private static final String modulus = "9590463682291452424391885648769462039632976899604535286569197548466087706310233496833906165630715753754777220171649332940480809145478261414302572155173011";
    private static final int MAX_ENCRYPT_BLOCK = 117;
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final String ALGORITHM_NAME = "RSA/None/PKCS1Padding";

    private RSAUtils() {
        throw new IllegalStateException();
    }

    private static RSAPublicKey getPublicKey() throws Exception {
        BigInteger b1 = new BigInteger(modulus);
        BigInteger b2 = new BigInteger(publicExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(b1, b2);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private static RSAPrivateKey getPrivateKey() throws Exception {
        BigInteger b1 = new BigInteger(modulus);
        BigInteger b2 = new BigInteger(privateExponent);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(b1, b2);
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }

    @NonNull
    public static byte[] encryptByPublicKey(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    @NonNull
    public static byte[] decryptByPublicKey(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.DECRYPT_MODE, getPublicKey());
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    @NonNull
    public static byte[] decryptByPrivateKey(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey());
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    @NonNull
    public static byte[] encryptByPrivateKey(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey());
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    public static byte[] longToBytes(long l) {
        byte[] result = new byte[Long.BYTES];
        for (int i = Long.BYTES-1; i >= 0; i--) {
            result[i] = (byte) (l & 0xFF);
            l >>= Long.BYTES;
        }
        return result;
    }

    public static byte[] longToBytesLe(long l) {
        byte[] result = new byte[Long.BYTES];
        for (int i = 0; i < Long.BYTES; i++) {
            result[i] = (byte) (l & 0xFF);
            l >>= Long.BYTES;
        }
        return result;
    }

    public static long bytesToLong(final byte[] bytes, final int offset) {
        long result = 0;
        for (int i = offset; i < Long.BYTES + offset; i++) {
            result <<= Long.BYTES;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    public static long bytesToLongLe(final byte[] bytes, final int offset) {
        byte[] longBytes = Arrays.copyOfRange(bytes, offset, offset + Long.BYTES);
        long result = 0;
        for (int i = Long.BYTES - 1; i >= 0; i--) {
            result <<= Long.BYTES;
            result |= (longBytes[i] & 0xFF);
        }
        return result;
    }
}
