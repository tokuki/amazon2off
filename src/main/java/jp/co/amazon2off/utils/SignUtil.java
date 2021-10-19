package jp.co.amazon2off.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.util.Random;

public class SignUtil {

    /**
     * AES/ECB/PKCS7Padding 加密
     */
    public static String encrypt(String sSrc, String sKey) {
        if (sKey == null) {
            throw new RuntimeException("加密内容为空");
        }

        if (sKey.length() != 16) {
            throw new RuntimeException("Key长度不是16位");
        }
        try {
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

            return new BigInteger(1, encrypted).toString(16);
        } catch (Exception e) {
            throw new RuntimeException("加密失败");
        }
    }

    /**
     * AES/ECB/PKCS7Padding 解密
     */
    public static String decrypt(String sSrc, String sKey) {
        if (sKey == null) {
            throw new RuntimeException("加密内容为空");
        }

        if (sKey.length() != 16) {
            throw new RuntimeException("Key长度不是16位");
        }
        try {
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] original = cipher.doFinal(hexToByteArray(sSrc));
            String originalString = new String(original, "utf-8");
            return originalString;
        } catch (Exception ex) {
            throw new RuntimeException("解密失败");
        }
    }

    /**
     * Hex转byte
     */
    public static byte[] hexToByteArray(String inHex) {
        int hexLen = inHex.length();
        byte[] result;
        if (hexLen % 2 == 1) {
            //奇数
            hexLen++;
            result = new byte[(hexLen / 2)];
            inHex = "0" + inHex;
        } else {
            //偶数
            result = new byte[(hexLen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexLen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    private static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static void main(String[] args) {
        StringBuffer stringBuffer = new StringBuffer();
        for(int j = 0; j< 6; j++){
            stringBuffer.append(new Random().nextInt(9));
        }
        System.out.println(stringBuffer);
    }

}
