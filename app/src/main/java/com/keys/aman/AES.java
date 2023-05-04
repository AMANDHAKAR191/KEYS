package com.keys.aman;

import android.content.Context;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* TODO Checked on 12/10/2022
    All is ok
 */
public class AES {
    private static AES sInstance;
    private static SecretKey key;

    private final int KEY_SIZE = 128;
    private static final int T_LEN = 128;
    private static byte[] IV;

    public void init() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    private static void initFromStrings(String aesKey, String aesIv) {
        key = new SecretKeySpec(decode(aesKey), "AES");
        IV = decode(aesIv);
    }
    public static AES getInstance(String aesKey, String aes_Iv) {
        if (sInstance == null) {
            sInstance = new AES();
            initFromStrings(aesKey,aes_Iv);
        }
        return sInstance;
    }

    public String encryptOld(String message) throws Exception {
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        IV = encryptionCipher.getIV();
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    private static String encrypt(String message) throws Exception {
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    private static String decrypt(String encryptedMessage) throws Exception {
        byte[] messageInBytes = decode(encryptedMessage);
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(messageInBytes);
        return new String(decryptedBytes);
    }

    public String singleEncryption(String data){
        try {
            String temp = encrypt(data);
            return encrypt(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String singleDecryption(String encryptionString){
        try {
            String temp = decrypt(encryptionString);
            return decrypt(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String doubleEncryption(String data){
        try {
            String temp = encrypt(data);
            return encrypt(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public String doubleDecryption(String encryptionString){
        try {
            String temp = decrypt(encryptionString);
            return decrypt(temp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private void exportKeys() {
        System.err.println("SecretKey : " + encode(key.getEncoded()));
        System.err.println("IV : " + encode(IV));
    }
}