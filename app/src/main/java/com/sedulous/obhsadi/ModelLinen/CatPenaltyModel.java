package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class CatPenaltyModel implements Serializable {
    /*
    @SerializedName("success")
    public int mStatus;
    @SerializedName("message")
    public String message;
     */
    @SerializedName("penaltyCategory")
    public ArrayList<CatItem> mCatItems;
    public static class CatItem implements Serializable {

        @SerializedName("id")
        public String mCatId;
        @SerializedName("cat_title")
        public String mCat_title;
        @SerializedName("status")
        public String mStatus;
        @SerializedName("del_status")
        public String mDel_status;
        @SerializedName("created_date")
        public String mCreated_date;
    }

}
