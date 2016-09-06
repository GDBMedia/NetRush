package com.netrush.netrushapp.utils;

import android.content.Context;

import com.netrush.netrushapp.Constants;
import com.netrush.netrushapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Garrett on 9/2/2016.
 */
public class DateHelper {
    public static String formatDate(String date, String source, Context context){
        String formattedDate = date;
        try{
            SimpleDateFormat sdfSource = new SimpleDateFormat(source);
            Date newdate = null;
            try {
                newdate = sdfSource.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(newdate);
            cal2.setTime(new Date());
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            boolean sameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
            if(sameDay){
                formattedDate = context.getString(R.string.today);
            }else if(!sameYear){
                formattedDate = new SimpleDateFormat(Constants.DATE_FORMAT_OUTPUT_YEAR).format(newdate);
            }else{
                formattedDate = new SimpleDateFormat(Constants.DATE_FORMAT_OUTPUT).format(newdate);
            }

        }catch(NullPointerException e){
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static double getDiffInDays(double timeStamp){
        Date date = new Date();
        long currenttime = date.getTime();
        double diff = currenttime - timeStamp;
        double diff_in_days =  (diff / (24*60*60*10*10*10));
        return diff_in_days;
    }
}
