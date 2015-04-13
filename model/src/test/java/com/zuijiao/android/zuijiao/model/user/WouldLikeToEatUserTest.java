package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Chen Hao on 3/18/15.
 */
public class WouldLikeToEatUserTest {

    private Gson gson;

    @Before
    public void setup() {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX").create();
    }

    @Test
    public void parseTest() {
        String rawJSON = "{" +
                "\t\t\"ID\": 108,\n" +
                "\t\t\"nickname\": \"ZuiJiaoTest\",\n" +
                "\t\t\"imageUrl\": \"\\/debug\\/Avatar\\/108_3.jpg\",\n" +
                "\t\t\"createdAt\": \"2015-03-10T11:22:01+08:00\",\n" +
                "\t\t\"collectionCount\": 3,\n" +
                "\t\t\"cuisineCount\": 15,\n" +
                "\t\t\"provinceID\": 0,\n" +
                "\t\t\"cityID\": 0\n" +
                "\t}";

        WouldLikeToEatUser model = gson.fromJson(rawJSON, WouldLikeToEatUser.class);

        assertThat(model.getIdentifier(), is(108));
        assertThat(model.getRecommendationCount(), is(15));
    }
}
