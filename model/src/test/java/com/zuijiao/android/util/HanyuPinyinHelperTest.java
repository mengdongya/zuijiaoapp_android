package com.zuijiao.android.util;

import org.junit.Test;

import static org.junit.Assert.fail;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Chen Hao on 5/6/15.
 */
public class HanyuPinyinHelperTest {

    @Test
    public void testGetPinyin() {
        String a = "中国人";
        assertThat(HanyuPinyinHelper.getPinyin(a), is("zhong guo ren"));

        String b = "Hello 中国";
        assertThat(HanyuPinyinHelper.getPinyin(b), is("Hello zhong guo"));
    }

}
