package com.keys.aman;

import android.util.Log;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/* TODO Checked on 12/10/2022
    All is ok
 */
public class AES implements iAES{
    private static AES sInstance;
    final public static String TAG = "AES";
    private static SecretKey key;

    private final int KEY_SIZE = 128;
    private static final int T_LEN = 128;
    private static byte[] IV;

    public void init() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(KEY_SIZE);
        key = generator.generateKey();
    }

    public static void initFromStrings(String aesKey, String aesIv) {
        key = new SecretKeySpec(sInstance.decode(aesKey), "AES");
        IV = sInstance.decode(aesIv);
        System.err.println("AES initialization Check Point ==> AES " + aesKey + " || IV" + aesIv);

    }
    public static AES getInstance(String aesKey, String aes_Iv) {
        if (sInstance == null) {
            sInstance = new AES();
        }
        initFromStrings(aesKey,aes_Iv);
        return sInstance;
    }
    //AES initialization Check Point ==> AES Rs7jnUSkq1sFrXkAk0evO7== || IVNp4ddPmgwBYCi7oS
    //AES initialization Check Point ==> AES Qw3rWsPhR9Pt7TLt2AsJgo== || IVHi66zMU1AmVfH6OP

    private String encryptOld(String message) throws Exception {
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key);
        IV = encryptionCipher.getIV();
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public String encrypt(String message) throws Exception {
        byte[] messageInBytes = message.getBytes();
        Cipher encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(T_LEN, IV);
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key, spec);
        byte[] encryptedBytes = encryptionCipher.doFinal(messageInBytes);
        return encode(encryptedBytes);
    }

    public String decrypt(String encryptedMessage) throws Exception {
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


    public String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    public byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    private void exportKeys() {
        System.err.println("SecretKey : " + encode(key.getEncoded()));
        System.err.println("IV : " + encode(IV));
    }
}