package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.util.functional.OneParameterExpression;
import com.zuijiao.android.zuijiao.model.common.Language;
import com.zuijiao.android.zuijiao.model.common.Languages;
import com.zuijiao.android.zuijiao.model.common.TasteTag;
import com.zuijiao.android.zuijiao.model.common.TasteTags;

import java.util.List;

/**
 * Created by Chen Hao on 3/30/15.
 */
public enum Cache {
    INSTANCE;

    public List<TasteTag> tasteTags;
    public List<String> gourmetTags;
    public List<Language> languages;

    public void setup() {
        Router.getCommonModule().gourmetTags(new OneParameterExpression<List<String>>() {
            @Override
            public void action(List<String> tags) {
                gourmetTags = tags;
            }
        }, null);

        Router.getCommonModule().tasteTags(new OneParameterExpression<TasteTags>() {
            @Override
            public void action(TasteTags tags) {
                tasteTags = tags.getTags();
            }
        }, null);

        Router.getCommonModule().languages(new OneParameterExpression<Languages>() {
            @Override
            public void action(Languages lan) {
                languages = lan.getLanguages();
            }
        }, null);



    }
}
