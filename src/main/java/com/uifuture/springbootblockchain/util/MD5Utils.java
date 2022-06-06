package com.uifuture.springbootblockchain.util;


import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * 采用MD5加密工具类
 */
public class MD5Utils {
    private static Logger logger = LoggerFactory.getLogger(MD5Utils.class);

    /***
     * MD5加密 生成32位md5码
     * @param inStr 待加密字符串
     * @return 返回32位md5码
     */
    public static String md5Encode(String inStr) {
        if(inStr==null || inStr.trim().length()==0){
            throw new RuntimeException("[MD5Utils-》md5Encode]字符串不能为空");
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            logger.error("MD5Util初始化异常: ", e);
            return "";
        }

        byte[] byteArray = new byte[0];
        byteArray = inStr.getBytes(StandardCharsets.UTF_8);

        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String md5Encode(byte[] byteArray) {
        if(byteArray==null || byteArray.length==0){
            throw new RuntimeException("[MD5Utils-》md5Encode]byteArray不能为空");
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            logger.error("MD5Util初始化异常: ", e);
            return "";
        }

        byte[] md5Bytes = md5.digest(byteArray);
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    public static String md5EncodeFile(String path) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        String md5 = DigestUtils.md5Hex(fileInputStream);
        fileInputStream.close();
        return md5;
    }
    /**
     * 测试主函数
     *
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        String str = new String("ceshiasdfds");
        System.out.println("原始：" + str);
        System.out.println("MD5后：" + md5Encode(str));
        long s = System.currentTimeMillis();
        BigInteger m = new BigInteger("30000");
        for (int i = 0; i < 10000000; i++) {
            BigInteger md5 = MD5Utils.md5ConvertNumber(""+i);
//            System.out.println(md5);
            BigInteger bigInteger = md5.mod(m);
            System.out.println(bigInteger.intValue());
        }
        long e = System.currentTimeMillis();
        System.out.println(e-s);
    }
    /**
     * 转换字节数组为16进制字串
     *
     * @param b 字节数组
     * @return 16进制字串
     */

    public static String byteArrayToString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToNumString(b[i]));//使用本函数则返回加密结果的10进制数字字串，即全数字形式
        }
        return resultSb.toString();

    }

    private static String byteToNumString(byte b) {
        int _b = b;
        if (_b < 0) {
            _b = 256 + _b;
        }
        return String.valueOf(_b);
    }

    /**
     * 字符串转整型
     * @param origin
     * @return
     */
    public static BigInteger md5ConvertNumber(String origin) {
        if(StringUtils.isEmpty(origin)){
            return new BigInteger("0");
        }
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToString(md.digest(origin.getBytes()));
        } catch (Exception ex) {
            logger.error("md5ConvertNumber 异常: ", ex);
            return new BigInteger("0");
        }
        return new BigInteger(resultString);
    }

}
