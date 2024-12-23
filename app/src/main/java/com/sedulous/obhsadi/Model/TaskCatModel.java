package com.sedulous.obhsadi.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class TaskCatModel implements Serializable {

    @SerializedName("status")
    public int mStatus;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public ArrayList<Task> mTasks;
    public static class Task implements Serializable{

        @SerializedName("id")
        public String mId;
        @SerializedName("question")
        public String mTask;

    }

}
