package com.kinneret.scaft.security;

import com.kinneret.scaft.utils.ByteManipulation;
import javafx.util.Pair;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.*;
import java.util.Base64;
import static com.kinneret.scaft.ui.Controller.conf;
import static com.kinneret.scaft.utils.CommonChars.*;

/**
 * A class which contains encrypt/decrypt API's of files and strings
 */
public class Security {

    private static Cipher cipher;
    private static MessageDigest digest;
    private static Charset utf8 = Charset.forName("UTF8");
    private static final int IV_SIZE = 16;

    static {
        try {
            cipher = Cipher.getInstance("AES/CTR/NoPadding");
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * A method to generate key, which encoded by utf-8 and hashed by SHA-256 algorithm
     * @return 256 bits key
     */
    private static byte[] generateKey() {
        ByteBuffer buffer = utf8.encode(conf.getPassword());
        byte[] keyBytes = new byte[buffer.remaining()];
        buffer.get(keyBytes);
        return digest.digest(keyBytes);
    }

    /**
     * A method to generate random IV
     * @return 128 bits random IV
     */
    private static byte[] generateRandomIV() {
        SecureRandom randomSecureRandom = new SecureRandom();
        byte[] iv = new byte[IV_SIZE];
        randomSecureRandom.nextBytes(iv);
        return iv;
    }

    /**
     * A method to encrypt a file
     * @param file - file to encrypt
     * @return encrypted file and metadata
     */
    public static String encryptFile(File file) {
        try {
            byte[] key = generateKey();
            byte[] ivBytes = generateRandomIV();
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encryptedIpPort = cipher.doFinal(utf8.encode(conf.getIp() + COLON + conf.getPort()).array());
            byte[] encryptedFileName = cipher.doFinal(utf8.encode(file.getName()).array());
            byte[] encryptedFile = cipher.doFinal( Base64.getEncoder().encode(Files.readAllBytes(file.toPath())));
            return ByteManipulation.bytesToHex(encryptedIpPort) +
                    SEPARATOR + ByteManipulation.bytesToHex(encryptedFileName) +
                    SEPARATOR + ByteManipulation.bytesToHex(encryptedFile) +
                    SEPARATOR + ByteManipulation.bytesToHex(ivBytes);
        } catch (IOException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * A method to decrypt a file
     * @param encryptedFile - file to decrypt
     * @return decrypted file with metadata
     */
    public static Pair<String, byte[]> decryptFile(String encryptedFile) {
        try {
            String[] tokens = encryptedFile.split(SEPARATOR);
            byte[] key = generateKey();
            byte[] encryptedIpPortBytes = ByteManipulation.hexToBytes(tokens[0]);
            byte[] encryptedFileNameBytes = ByteManipulation.hexToBytes(tokens[1]);
            byte[] encryptedFileBytes = ByteManipulation.hexToBytes(tokens[2]);
            byte[] ivBytes = ByteManipulation.hexToBytes(tokens[3]);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            String ipPort = utf8.decode(ByteBuffer.wrap(cipher.doFinal(encryptedIpPortBytes))).toString();
            String fileName = utf8.decode(ByteBuffer.wrap(cipher.doFinal(encryptedFileNameBytes))).toString();
            byte[] decryptedFile = Base64.getDecoder().decode(cipher.doFinal(encryptedFileBytes));
            return new Pair<>(ipPort + SLASH + fileName, decryptedFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method to encrypt a message
     * @param message - message to decrypt
     * @return encrypted message
     */
    public static String encryptMessage(String message) {
        try {
            byte[] key = generateKey();
            byte[] ivBytes = generateRandomIV();
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
            byte[] encrypted = cipher.doFinal(utf8.encode(message).array());
            return ByteManipulation.bytesToHex(encrypted) + SEPARATOR + ByteManipulation.bytesToHex(ivBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * A method to decrypt a message
     * @param message - message to decrypt
     * @return decrypted message
     */
    public static Pair<String, String> decryptMessage(String message) {
        try {
            byte[] key = generateKey();
            String[] messageAndIV = message.trim().split(SEPARATOR);
            byte[] encryptedMessage = ByteManipulation.hexToBytes(messageAndIV[0]);
            byte[] ivBytes = ByteManipulation.hexToBytes(messageAndIV[1]);
            SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
            byte[] plainText = cipher.doFinal(encryptedMessage);
            ByteBuffer buffer = ByteBuffer.wrap(plainText);
            return new Pair<>(messageAndIV[1], utf8.decode(buffer).toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
