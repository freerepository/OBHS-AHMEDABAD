package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserDataModel implements Serializable {
    @SerializedName("status")
    public int mstatus;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public ArrayList<UserLogin> mUserItems;

    public static class UserLogin implements Serializable {
        @SerializedName("user_id")
        public String mUser_id;
        @SerializedName("user_encrypt_id")
        public String mUser_encrypt_id;
        @SerializedName("user_email")
        public String mUser_email;
        @SerializedName("address")
        public String mAddress;
        @SerializedName("user_type")
        public String mUser_type;
        @SerializedName("designation")
        public String mDesignation;
        @SerializedName("depot_code")
        public String mDepot_code;
//        @SerializedName("depot_code")
//        public String mDepot_code;
    }
}
