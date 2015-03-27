package com.zuijiao.android.zuijiao.model;

import com.google.gson.GsonBuilder;
import com.zuijiao.android.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Chen Hao on 3/18/15.
 */
public class GourmetTest {

    private GsonBuilder gsonBuilder;

    @Before
    public void setup() {
        gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
    }

    @Test
    public void parseTest() {
        String rawJSON = "{\n" +
                "\t\"apiVersion\": \"1.0\",\n" +
                "\t\"kind\": \"foods#cuisines\",\n" +
                "\t\"itemCount\": 4,\n" +
                "\t\"itemTotalCount\": 4,\n" +
                "\t\"items\": [{\n" +
                "\t\t\"ID\": 548,\n" +
                "\t\t\"title\": \"\\u5728\",\n" +
                "\t\t\"isFeast\": false,\n" +
                "\t\t\"cityID\": 4,\n" +
                "\t\t\"provinceID\": 4,\n" +
                "\t\t\"address\": \"\",\n" +
                "\t\t\"description\": \"\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u54e6\\uff1f\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\\u72d7\",\n" +
                "\t\t\"price\": 0,\n" +
                "\t\t\"tags\": [],\n" +
                "\t\t\"imageUrls\": [],\n" +
                "\t\t\"createdAt\": \"2015-03-18T12:42:46+08:00\",\n" +
                "\t\t\"updatedAt\": \"2015-03-18T12:42:46+08:00\",\n" +
                "\t\t\"commentCount\": 0,\n" +
                "\t\t\"collectionCount\": 0,\n" +
                "\t\t\"isCollected\": false,\n" +
                "\t\t\"user\": {\n" +
                "\t\t\t\"ID\": 239,\n" +
                "\t\t\t\"nickname\": \"2\",\n" +
                "\t\t\t\"imageUrl\": \"\"\n" +
                "\t\t}\n" +
                "\t}," +
                "{\n" +
                "\t\t\"ID\": 542,\n" +
                "\t\t\"title\": \"jklj\",\n" +
                "\t\t\"isFeast\": true,\n" +
                "\t\t\"cityID\": 4,\n" +
                "\t\t\"provinceID\": 4,\n" +
                "\t\t\"address\": \"q\",\n" +
                "\t\t\"description\": \"kljklj\",\n" +
                "\t\t\"price\": 25,\n" +
                "\t\t\"tags\": [],\n" +
                "\t\t\"imageUrls\": [\"http:\\/\\/pic.zuijiao.net\\/debug\\/Gourmet\\/239_jklj_20150377-111416-1.jpeg\", \"http:\\/\\/pic.zuijiao.net\\/debug\\/Gourmet\\/239_jklj_20150377-111416-0.jpeg\", \"http:\\/\\/pic.zuijiao.net\\/debug\\/Gourmet\\/239_jklj_20150377-111416-2.jpeg\"],\n" +
                "\t\t\"createdAt\": \"2015-03-18T11:14:18+08:00\",\n" +
                "\t\t\"updatedAt\": \"2015-03-18T11:14:43+08:00\",\n" +
                "\t\t\"commentCount\": 0,\n" +
                "\t\t\"collectionCount\": 0,\n" +
                "\t\t\"isCollected\": false,\n" +
                "\t\t\"user\": {\n" +
                "\t\t\t\"ID\": 239,\n" +
                "\t\t\t\"nickname\": \"2\",\n" +
                "\t\t\t\"imageUrl\": \"\"\n" +
                "\t\t}\n" +
                "\t}]" +
                "}";

        Gourmets model = gsonBuilder.create().fromJson(rawJSON, Gourmets.class);

        assertThat(model.getTotalCount(), is(4));

        Gourmet gourmet = model.getGourmets().get(0);

        assertThat(gourmet.getIdentifier(), is(548));
        assertThat(gourmet.getName(), is("在"));
        assertThat(gourmet.getIsPrivate(), is(false));
        assertThat(gourmet.getImageURLs().size(), is(0));
        assertThat(gourmet.getTags().size(), is(0));
        assertThat(gourmet.getAddress(), is(""));
        assertThat(gourmet.getPrice(), is("0"));

        // check user
        assertThat(gourmet.getUser().getIdentifier(), is(239));
        assertThat(gourmet.getUser().getNickName(), is("2"));
        assertThat(gourmet.getUser().getAvatarURL(), is(Optional.of("")));

        Gourmet gourmetWithImage = model.getGourmets().get(1);
        assertThat(gourmetWithImage.getImageURLs().get(0), is("http://pic.zuijiao.net/debug/Gourmet/239_jklj_20150377-111416-1.jpeg"));

    }

}
