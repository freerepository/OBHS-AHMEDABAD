package com.sedulous.obhsadi.service;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by algosofttechnologies on 2/21/18.
 */

public class Util {

    public ArrayList<Integer> totalAmount;

    public static String createTimeAudioFileName()
    {
        Calendar calendar=Calendar.getInstance();
        float day=calendar.getTime().getDate();
        float month=calendar.getTime().getMonth()+1;
        float year=calendar.getTime().getYear();
        float hours=calendar.getTime().getHours();
        float minutes=calendar.getTime().getMinutes();
        float seconds=calendar.getTime().getSeconds();

        return (int)hours+"_"+(int)minutes+"_"+(int)seconds;
    }

    public static String before(String value, String a) {
        // Return substring containing all characters before a string.
        int posA = value.indexOf(a);
        if (posA == -1) {
            return "";
        }
        return value.substring(0, posA);
    }

    public static String after(String value, String a) {
        // Returns a substring containing all characters after a string.
        int posA = value.lastIndexOf(a);
        if (posA == -1) {
            return "";
        }
        int adjustedPosA = posA + a.length();
        if (adjustedPosA >= value.length()) {
            return "";
        }
        return value.substring(adjustedPosA);
    }


    public ArrayList<Integer> getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(ArrayList<Integer> totalAmount) {
        this.totalAmount = totalAmount;
    }
}
