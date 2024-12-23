package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class AttendanceListModel implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("Getusers")
    public ArrayList<UserItem> mUserItemList;

    public static class UserItem implements Serializable {
        @SerializedName("id")
        public String id;
        @SerializedName("EMPM_Name")
        public String EMPM_Name;
        @SerializedName("EMPM_type")
        public String EMPM_type;
        @SerializedName("RAPD_BioID")
        public String RAPD_BioID;
        @SerializedName("coach")
        public String mCoach;
        @SerializedName("RAPD_DeviceId")
        public String RAPD_DeviceId;
        @SerializedName("RAPD_DeviceUniqueID")
        public String RAPD_DeviceUniqueID;
        @SerializedName("RAPD_Field1")
        public String RAPD_Field1;
        @SerializedName("RAPD_Field2")
        public String RAPD_Field2;
        @SerializedName("RAPD_Field3")
        public String RAPD_Field3;
        @SerializedName("RAPD_Field4")
        public String RAPD_Field4;
        @SerializedName("RAPD_Field5")
        public String RAPD_Field5;
        @SerializedName("RAPD_FingerQuality")
        public String RAPD_FingerQuality;
        @SerializedName("RAPD_IsProcessed")
        public String RAPD_IsProcessed;
        @SerializedName("RAPD_PunchDateTime")
        public String RAPD_PunchDateTime;
        @SerializedName("date")
        public String mdate;
        @SerializedName("RAPD_latitude")
        public String RAPD_latitude;
        @SerializedName("RAPD_longitude")
        public String RAPD_longitude;
        @SerializedName("address")
        public String address;
        @SerializedName("Remark")
        public String Remark;
        @SerializedName("depot_code")
        public String depot_code;
        @SerializedName("attendance_by")
        public String attendance_by;
        @SerializedName("user_image")
        public String user_image;
        @SerializedName("creation_date")
        public String creation_date;
        @SerializedName("attendance_complete")
        public String attendance_complete;

    }
}
