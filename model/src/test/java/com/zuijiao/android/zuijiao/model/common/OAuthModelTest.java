package com.zuijiao.android.zuijiao.model.common;

import com.google.gson.GsonBuilder;
import com.zuijiao.android.util.Optional;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Chen Hao on 3/16/15.
 */
public class OAuthModelTest {

    private GsonBuilder gsonBuilder;

    @Before
    public void setup() {
        gsonBuilder = new GsonBuilder().serializeNulls();
    }

    //    @Test
    public void loginParseTest() {
        String fullJSON = "{\n" +
                "\t\"access_token\": \"1bv4M3I394LkWUb0LFbXL5KBh0gwOcdAEmZL86Vi\",\n" +
                "\t\"token_type\": \"Bearer\",\n" +
                "\t\"expires_in\": 86400,\n" +
                "\t\"user\": {\n" +
                "\t\t\"ID\": 120,\n" +
                "\t\t\"nickname\": \"\\ud83d\\udc7b\",\n" +
                "\t\t\"imageUrls\": \"http:\\/\\/wx.qlogo.cn\\/mmopen\\/ZzfOoev6lEWf63CczF8Is0HTKx4qVu0DpS9N0yuc9ES2g2QQILKSeqgTeXcsBB2fWgbVuNgLKWou8vHSEicdSm0NJ5nd9wVmx\\/0\"\n" +
                "\t},\n" +
                "\t\"isNew\": false\n" +
                "}";

        OAuthModel model = gsonBuilder.create().fromJson(fullJSON, OAuthModel.class);

        if (model.getUser().isPresent()) {
            assertThat(model.getUser().get().getIdentifier(), is(120));
        }

        assertThat(model.getIsNew(), is(false));
    }

    @Test
    public void visitorParseTest() {
        String json = "{\n" +
                "\t\"access_token\": \"1bv4M3I394LkWUb0LFbXL5KBh0gwOcdAEmZL86Vi\",\n" +
                "\t\"token_type\": \"Bearer\",\n" +
                "\t\"expires_in\": 86400}";
        OAuthModel model = gsonBuilder.create().fromJson(json, OAuthModel.class);

        assertThat(model.getUser(), is(Optional.empty()));
        assertThat(model.getIsNew(), is(false));

    }

}
