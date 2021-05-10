package com.haylion.android.data.util;

import java.math.BigInteger;
import java.security.MessageDigest;

public class HashUtil {

    public static final String ALGORITHM_SHA512 = "SHA-512";

    public static final String ALGORITHM_MD5 = "md5";

    public static final int HEX_16 = 16;

/*    *//**
     * 用户密码加密 ：md5(md5(password+salt))
     *
     * @param plainText 明文
     * @param salt      盐: Date.getTime()
     * @return 加密后密文
     *//*
    public static String encryptUserPasswordByMD5(String plainText, String salt) {

        String cipherText = plainText + salt;
        cipherText = md5(cipherText);
        return cipherText;
    }*/

    /**
     * MD5加密
     * @param data 要加密的数据
     * @return 哈希加密后16进制字符串
     */
    public static String md5(String data) throws Exception {
        return hash(ALGORITHM_MD5,data,HEX_16);
    }

    /**
     * @param algo 要使用的哈希算法，例如："md5"，"sha256"，"haval160,4" 等。
     * @param data 要进行哈希运算的消息。
     * @param hex  字符串表示形式的基数。
     * @return String
     */
    public static String hash(String algo, String data, int hex) throws Exception {

        try {
            MessageDigest md = MessageDigest.getInstance(algo);
            return new BigInteger(1, md.digest(data.getBytes())).toString(hex);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("加密");
        }
    }


/*    *//**
     * MD5加密，重置密码，是首先使用不加盐的加密，然后再使用加盐再加密
     * *//*
    public static String resetPassword(String password, String salt) {
        String md5 = DigestUtils.md5DigestAsHex(password.getBytes());
        return encryptUserPasswordByMD5(md5, salt);
    }*/
}
