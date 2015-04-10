package com.zuijiao.utils;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    public static String crypt(String str) throws NoSuchAlgorithmException


    {
        if (str == null || str.length() == 0) {
            throw new IllegalArgumentException("String to encript cannot be null or zero length");
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(str.getBytes());
        byte[] hash = md.digest();
        return hashByte2MD5(hash);
    }


    public static String crypt(byte[] bytes) throws NoSuchAlgorithmException {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("bytes to encript cannot be null or zero length");
        }
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hash = digest.digest(bytes);
        return hashByte2MD5(hash);
    }


    public static String crypt(InputStream in) throws NoSuchAlgorithmException, IOException {
        if (in == null || in.available() == 0) {
            throw new IllegalArgumentException("InputStream can't be null or zero length.");
        }
        MessageDigest digest = MessageDigest.getInstance("MD5");
        try {
            byte[] buff = new byte[4096];
            int len = 0;
            while ((len = in.read(buff, 0, buff.length)) >= 0) {
                digest.update(buff, 0, len);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
        byte[] hash = digest.digest();
        return hashByte2MD5(hash);
    }


    private static String hashByte2MD5(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                hexString.append("0" + Integer.toHexString((0xFF & hash[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        }

        return hexString.toString();
    }

}

