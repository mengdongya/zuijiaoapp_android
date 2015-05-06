package com.zuijiao.android.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by Chen Hao on 5/6/15.
 */
public final class HanyuPinyinHelper {

    private static HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();

    public static String getPinyin(String s) {
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        format.setVCharType(HanyuPinyinVCharType.WITH_V);

        char[] input = s.trim().toCharArray();
        StringBuilder stringBuilder = new StringBuilder();

        try {
            for (int i = 0; i < input.length; i++) {
                if (Character.toString(input[i]).matches("[\u4E00-\u9FA5]+")) {
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
                    stringBuilder.append(temp[0]);
                    stringBuilder.append(" ");
                } else {
                    stringBuilder.append(Character.toString(input[i]));
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        return stringBuilder.toString().trim();
    }

}
