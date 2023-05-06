package com.keys.aman;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;

public interface iAES {
    String encrypt(String message)throws Exception;

    String decrypt(String encryptedMessage)throws Exception;

    String singleEncryption(String data);

    String singleDecryption(String encryptionString);

    String doubleEncryption(String data);

    String doubleDecryption(String encryptionString);

    String encode(byte[] data);

    byte[] decode(String data);

}
