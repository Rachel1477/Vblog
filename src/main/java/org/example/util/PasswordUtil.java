package org.example.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 密码加密工具类 - SHA-256
 */
public class PasswordUtil {

    /**
     * 使用SHA-256加密密码
     *
     * @param password 明文密码
     * @return SHA-256加密后的密码
     */
    public static String encrypt(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 加密失败", e);
        }
    }

    /**
     * 验证密码
     *
     * @param inputPassword 输入的明文密码
     * @param storedPassword 存储的加密密码
     * @return 是否匹配
     */
    public static boolean verify(String inputPassword, String storedPassword) {
        String encryptedInput = encrypt(inputPassword);
        return encryptedInput.equals(storedPassword);
    }

    /**
     * 字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}

