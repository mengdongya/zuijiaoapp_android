package com.zuijiao.android.zuijiao.model.user;

import com.google.gson.Gson;
import com.zuijiao.android.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Chen Hao on 3/23/15.
 */
public class UserModelTest {

    String rawJson = "";

    @Before
    public void setup() {
        rawJson = "{\n" +
                "\t\"apiVersion\": \"1.0\",\n" +
                "\t\"kind\": \"users#person\",\n" +
                "\t\"ID\": 258,\n" +
                "\t\"nickname\": \"2\",\n" +
                "\t\"imageUrl\": \"\",\n" +
                "\t\"profile\": {\n" +
                "\t\t\"birthday\": null,\n" +
                "\t\t\"gender\": null,\n" +
                "\t\t\"provinceID\": 0,\n" +
                "\t\t\"cityID\": 0,\n" +
                "\t\t\"story\": \"2\",\n" +
                "\t\t\"tasteTags\": []\n" +
                "\t},\n" +
                "\t\"friendships\": {\n" +
                "\t\t\"followingCount\": 0,\n" +
                "\t\t\"followerCount\": 0,\n" +
                "\t\t\"isFollowing\": false,\n" +
                "\t\t\"isFollower\": false\n" +
                "\t},\n" +
                "\t\"foods\": {\n" +
                "\t\t\"collectionCount\": 0,\n" +
                "\t\t\"cuisineCount\": 1\n" +
                "\t}\n" +
                "}";
    }

    @Test
    public void parseTest() {
        User user = new Gson().fromJson(rawJson, User.class);
        assertThat(user.getIdentifier(), is(258));
        assertThat(user.getNickname(), is(Optional.of("2")));
        assertThat(user.getGender(), is(Optional.empty()));
        assertThat(user.getFollowerCount(), is(0));
        assertThat(user.getRecommendationCount(), is(1));
    }
}
