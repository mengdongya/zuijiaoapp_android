package com.zuijiao.android.zuijiao.network;

import com.zuijiao.android.zuijiao.model.common.TasteTag;

import java.util.List;

/**
 * Created by Chen Hao on 3/30/15.
 */
public enum Cache {
    INSTANCE;

    private List<TasteTag> tasteTags;
    private List<String> gourmetTags;

    public void setup() {
        Router.getCommonModule().gourmetTags((List<String> gourmetTags) -> this.gourmetTags = gourmetTags
                , null);

        Router.getCommonModule().tasteTags(tasteTags -> this.tasteTags = tasteTags.getTags()
                , null);
    }
}
