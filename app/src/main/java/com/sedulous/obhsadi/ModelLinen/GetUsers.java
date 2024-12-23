package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class GetUsers implements Serializable {
    @SerializedName("user_data")
    public ArrayList<User> getUsername;

    public static class User implements Serializable {
        @SerializedName("name")
        public String nName;
    }
}