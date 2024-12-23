package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SaveAttendance implements Serializable {
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
    @SerializedName("class")
    public String mClass;
    @SerializedName("attendent_name")
    public String mAttendent_name;
    @SerializedName("idcard_no")
    public String mIdcard_no;
    @SerializedName("bedsheet")
    public String mBedsheet;
    @SerializedName("pillow")
    public String mPillow;
    @SerializedName("pillow_cover")
    public String mPillow_cover;
    @SerializedName("bath_towel")
    public String mBath_towel;
    @SerializedName("face_towel")
    public String mFace_towel;
    @SerializedName("blanket")
    public String mBlanket;
    @SerializedName("item_image")
    public String mItem_image;
    @SerializedName("item_image1")
    public String mItem_image1;
    @SerializedName("signature")
    public String mSignature;
    @SerializedName("signature1")
    public String mSignature1;
}
