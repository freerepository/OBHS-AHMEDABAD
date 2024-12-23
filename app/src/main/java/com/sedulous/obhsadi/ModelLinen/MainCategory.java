package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MainCategory implements Serializable{
    @SerializedName("message")
    public String message;
    @SerializedName("GetCategory")

    public ArrayList<Item> CatList;

    public static class Item implements Serializable {
        @SerializedName("id")
        public String mID;
        @SerializedName("category")
        public String mCategory;

    }
}
