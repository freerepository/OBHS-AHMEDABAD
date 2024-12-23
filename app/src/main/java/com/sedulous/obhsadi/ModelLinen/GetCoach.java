package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetCoach implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("coachList")

    public ArrayList<Item> mCoachList;

    public static class Item implements Serializable {
        @SerializedName("id")
        public String mID;
        @SerializedName("coach")
        public String mCoach;
    }
}

