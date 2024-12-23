package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LinenQuestionData implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("lineItemsList")

    public ArrayList<Question> mItemList;

    public static class Question implements Serializable {
        @SerializedName("id")
        public String mID;
        @SerializedName("supervisor_id")
        public String mSupervisor_id;
        @SerializedName("journey_date")
        public String mJourney_date;
        @SerializedName("station")
        public String mStation;
        @SerializedName("train_no")
        public String mTrain_no;
        @SerializedName("coach")
        public String mCoach;
        @SerializedName("class_type")
        public String mClass_type;
        @SerializedName("attendent_name")
        public String mAttendent_name;
        @SerializedName("idcard_no")
        public String mIdcard_no;
        @SerializedName("item_name")
        public String mItem_name;
        @SerializedName("item_total")
        public String mItem_total;
        @SerializedName("amount")
        public String mAmount;


    }
}

