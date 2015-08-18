package com.zuijiao.android.zuijiao.model.Banquent;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaqibo on 2015/8/17.
 */
public class SellerAccount {
    @SerializedName("name")
    private  String name ;
    @SerializedName("bank")
    private String bank ;

    public String getCard() {
        return card;
    }

    public String getBank() {
        return bank;
    }

    public String getName() {
        return name;
    }

    @SerializedName("card")
    private String card ;
}
