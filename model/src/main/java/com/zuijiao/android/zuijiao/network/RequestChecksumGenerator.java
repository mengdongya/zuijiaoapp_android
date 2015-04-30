package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.MD5;

import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;

/**
 * Created by Chen Hao on 4/30/15.
 */
public class RequestChecksumGenerator {

    private static String getRandom() {
        CRC32 crc32 = new CRC32();
        Long timeMillis = Long.valueOf(System.currentTimeMillis());
        crc32.update(timeMillis.byteValue());
        return Long.toHexString(crc32.getValue());
    }

    static String generateCheckSum(String key) {
        String hash;
        String random = getRandom();
        key = key == null ? random : key;
        try {
            hash = MD5.crypt(key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            hash = "42b90196b487c54069097a68fe98ab6f";
        }
        return String.format("%s%s", random, hash);
    }

}
