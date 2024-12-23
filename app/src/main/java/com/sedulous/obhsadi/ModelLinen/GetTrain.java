package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetTrain implements Serializable {
    @SerializedName("TrainList")
    public ArrayList<Item> mTrainList;

    public static class Item implements Serializable {
        @SerializedName("id")
        public String mID;
        @SerializedName("station_code")
        public String mStation_code;
        @SerializedName("train_no")
        public String mTrain_no;
    }
}


