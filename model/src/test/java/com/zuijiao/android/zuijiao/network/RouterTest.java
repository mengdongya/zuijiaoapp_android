package com.zuijiao.android.zuijiao.network;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Chen Hao on 3/23/15.
 */
public class RouterTest {

    @Test
    public void convertJsonFromListTest() {
        assertThat("[]", is(Router.convertJsonFromList(new ArrayList<String>())));

        List<String> urls = new ArrayList<>();
        urls.add("1");
        urls.add("3");
        assertThat("[\"1\",\"3\"]", is(Router.convertJsonFromList(urls)));


    }
}
