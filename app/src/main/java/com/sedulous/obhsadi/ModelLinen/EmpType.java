package com.sedulous.obhsadi.ModelLinen;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class EmpType implements Serializable {
    @SerializedName("message")
    public String message;
    @SerializedName("Getdata")
    public ArrayList<Item> mEmpType;

    public static class Item implements Serializable {
        @SerializedName("EMPM_name")
        public String mEMPM_name;
        @SerializedName("EMPM_type")
        public String mEMPM_type;

}
}
