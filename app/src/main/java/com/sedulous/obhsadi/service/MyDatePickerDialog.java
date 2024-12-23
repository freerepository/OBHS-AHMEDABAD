package com.sedulous.obhsadi.service;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import com.sedulous.obhsadi.R;

/**
 * object of this class is used meny times in project to choose date by calender dialod
 */
public class MyDatePickerDialog extends DatePickerDialog {
    Context context;

    public MyDatePickerDialog(Context context,
                              OnDateSetListener callBack, int year, int monthOfYear,
                              int dayOfMonth) {
        super(context, R.style.AppTheme, callBack, year, monthOfYear, dayOfMonth);
        this.context = context;
        //this.setTitle(year + "-" + (monthOfYear + 1) + "-");
        DatePicker dp = getDatePicker();
        //set limit to current date
        dp.setMaxDate(System.currentTimeMillis());
//        Calendar calendar = Calendar.getInstance();
//        Date today = calendar.getTime();
//        calendar.add(Calendar.DAY_OF_YEAR, 1);
//        Date tomorrow = calendar.getTime();
//        dp.setMinDate(tomorrow.getTime());
        //click on year button on starting
        //dp.getTouchables().get(0).performClick();
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        super.onDateChanged(view, year, month, day);
        //this.setTitle(year + "-" + (month + 1) + "-");
    }

}