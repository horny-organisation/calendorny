package ru.calendorny.zoommeetingservice.service;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AesEncryptionService {

    private static final String AES = "AES";
    private static final String AES_GCM_NO_PADDING = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH_BIT = 128;

    private final byte[] key;

    public AesEncryptionService(@Value("${encryption.aes.key}") String base64Key) {
        this.key = Base64.getDecoder().decode(base64Key);
    }

    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[IV_SIZE];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, AES), spec);

            byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] encryptedIvAndText = new byte[IV_SIZE + encrypted.length];
            System.arraycopy(iv, 0, encryptedIvAndText, 0, IV_SIZE);
            System.arraycopy(encrypted, 0, encryptedIvAndText, IV_SIZE, encrypted.length);

            return Base64.getEncoder().encodeToString(encryptedIvAndText);
        } catch (Exception e) {
            throw new RuntimeException("Error during encryption", e);
        }
    }

    public String decrypt(String encryptedBase64) {
        try {
            byte[] encryptedIvTextBytes = Base64.getDecoder().decode(encryptedBase64);

            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(encryptedIvTextBytes, 0, iv, 0, IV_SIZE);

            int encryptedSize = encryptedIvTextBytes.length - IV_SIZE;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(encryptedIvTextBytes, IV_SIZE, encryptedBytes, 0, encryptedSize);

            Cipher cipher = Cipher.getInstance(AES_GCM_NO_PADDING);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, AES), spec);

            byte[] decrypted = cipher.doFinal(encryptedBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error during decryption", e);
        }
    }
}
