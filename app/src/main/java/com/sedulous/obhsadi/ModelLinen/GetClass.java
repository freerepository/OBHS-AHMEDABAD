package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetClass implements Serializable {
    @SerializedName("classType")

    public ArrayList<Item> mClassList;

    public static class Item implements Serializable {
        @SerializedName("id")
        public String mID;
        @SerializedName("class")
        public String mClass;
    }
}

