package com.sedulous.obhsadi.Model;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class UserStationModel implements Serializable {

    @SerializedName("Getsdata")
    public ArrayList<UserStationModel.StationData> stationList;

    public static class StationData implements Serializable {
        @SerializedName("id")
        public String id;

        @SerializedName("train_no")
        public String trainNo;

        @SerializedName("station")
        public String station;

        @SerializedName("latitude")
        public String latitude;

        @SerializedName("longitude")
        public String longitude;

        @SerializedName("location")
        public String location;

        @SerializedName("status")
        public String status;
    }
}

