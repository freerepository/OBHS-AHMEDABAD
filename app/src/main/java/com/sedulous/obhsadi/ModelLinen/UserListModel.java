package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserListModel  implements Serializable {
//    @SerializedName("message")
//    public String message;
    @SerializedName("user_data")
    public ArrayList<UserListModel.UserItem> mUserList;

    public static class UserItem implements Serializable {
        @SerializedName("id")
        public String id;
        @SerializedName("depot_code")
        public String mDepot_code;
        @SerializedName("EMPM_userID")
        public String mEMPM_userID;
        @SerializedName("EMPM_name")
        public String mEMPM_name;
        @SerializedName("EMPM_type")
        public String mEMPM_type;
        @SerializedName("EMPM_skill_type")
        public String mEMPM_skill_type;
        @SerializedName("EMPM_login_method")
        public String mEMPM_login_method;
        @SerializedName("EMPM_password")
        public String mEMPM_password;
        @SerializedName("EMPM_phone_no")
        public String mEMPM_phone_no;
        @SerializedName("EMPM_email")
        public String mEMPM_email;
        @SerializedName("EMPM_address")
        public String mEMPM_address;
        @SerializedName("police_verification")
        public String mPolice_verification;
        @SerializedName("EMPM_picture")
        public String mEMPM_picture;
        @SerializedName("EMPM_picture_url")
        public String mEMPM_picture_url;
        @SerializedName("del_status")
        public String mDel_status;
        @SerializedName("act_status")
        public String mAct_status;
        @SerializedName("created_date")
        public String mCreated_date;
        @SerializedName("updated")
        public String mUpdated;


    }
}
