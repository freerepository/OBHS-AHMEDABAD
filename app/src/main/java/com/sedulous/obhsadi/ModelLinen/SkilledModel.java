package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SkilledModel implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("Skilled Type List")
    public String SkilledTypeList;
    @SerializedName("Getskilledtype")
    public ArrayList<Item> mSkilledList;

    public static class Item implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("skilled_type")
        public String mSkilled_type;
        @SerializedName("act_status")
        public String mAct_status;
        @SerializedName("del_status")
        public String mDel_status;

    }
}
