package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EmpModel implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("Users Type List")
    public String UsersTypeList;
    @SerializedName("Getuserstype")
    public ArrayList<Item> mEmpList;

    public static class Item implements Serializable {
        @SerializedName("id")
        public String mId;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("user_type")
        public String mUser_type;
        @SerializedName("user_type_name")
        public String mUser_type_name;
        @SerializedName("act_status")
        public String mAct_status;
        @SerializedName("del_status")
        public String mDel_status;

    }
}
