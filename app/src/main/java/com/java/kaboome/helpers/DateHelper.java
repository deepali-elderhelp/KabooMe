/*
 * *
 *  * Created by KabooMe, Inc.
 *  * Copyright (c) 2019 . All rights reserved.
 *
 */

package com.java.kaboome.helpers;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateHelper {

    private static final String TAG = "KMDateHelper";


    public static boolean isTodaysDate(Calendar calendar){
    if(calendar == null)
        return false;
    Calendar todaysDate = Calendar.getInstance();
    return (todaysDate.get(Calendar.ERA) == calendar.get(Calendar.ERA) &&
            todaysDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
            todaysDate.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR));
}

    public static String getDateFormatted(long dateInMilliSeconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(dateInMilliSeconds);
    }

    public static String getDateFormattedPretty(long dateInMilliSeconds){
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(dateInMilliSeconds);
    }

    public static String getJustDate(long dateInMilliSeconds){
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");
        Date date = new Date(dateInMilliSeconds);


        return formatter.format(dateInMilliSeconds);
    }

    public static String dateForChatMessages(long dateInMilliSeconds){
        SimpleDateFormat regularFormatter = new SimpleDateFormat("dd MMM");
        SimpleDateFormat withYearFormatter = new SimpleDateFormat("dd MMM, YYYY");
        SimpleDateFormat justYearFormatter = new SimpleDateFormat("YYYY");
        SimpleDateFormat basicFormatter = new SimpleDateFormat("dd-MM-yyyy");


        Date today = new Date();
        long todaysDateInMilliseconds = today.getTime();
        long yesterdayDateInMilliseconds = todaysDateInMilliseconds - 1000L * 24L * 60L * 60L;

        String todaysDateInFormat = basicFormatter.format(today);
        String yesterdayDateInFormat = basicFormatter.format(yesterdayDateInMilliseconds);
        String passedDateInFormat = basicFormatter.format(dateInMilliSeconds);
        String passedDateYear = justYearFormatter.format(dateInMilliSeconds);
        String currentYear = justYearFormatter.format(today);

        if(todaysDateInFormat.equals(passedDateInFormat))
            return ("Today");
        else if(yesterdayDateInFormat.equals(passedDateInFormat))
            return ("Yesterday");
        else{
            if(currentYear.equals(passedDateYear)){
                return (regularFormatter.format(dateInMilliSeconds));
            }
            else{
                return (withYearFormatter.format(dateInMilliSeconds));
            }
        }

    }

    public static String getDateFormatted(String stringDateTimeFormat) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = inputFormat.parse(stringDateTimeFormat);
        String formattedDate = outputFormat.format(date);
        return formattedDate;
    }

    public static String getPrettyDate(long dateInMilliSeconds){

        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, h:mm a");
        SimpleDateFormat onlyTimeFormatter = new SimpleDateFormat("h:mm a");
        SimpleDateFormat basicFormatter = new SimpleDateFormat("dd-MM-yyyy");

        Date today = new Date();
        long todaysDateInMilliseconds = today.getTime();
        long yesterdayDateInMilliseconds = todaysDateInMilliseconds - 1000L * 24L * 60L * 60L;

        String todaysDateInFormat = basicFormatter.format(today);
        String yesterdayDateInFormat = basicFormatter.format(yesterdayDateInMilliseconds);
        String passedDateInFormat = basicFormatter.format(dateInMilliSeconds);

        if(todaysDateInFormat.equals(passedDateInFormat))
            return ("Today "+onlyTimeFormatter.format(dateInMilliSeconds));
        else if(yesterdayDateInFormat.equals(passedDateInFormat))
            return ("Yesterday "+onlyTimeFormatter.format(dateInMilliSeconds));
        else
            return (formatter.format(dateInMilliSeconds));


    }

    public static String getPrettyTime(long dateInMilliSeconds){

        SimpleDateFormat formatter = new SimpleDateFormat("MMM d, h:mm a");
        SimpleDateFormat onlyTimeFormatter = new SimpleDateFormat("h:mm a");
        SimpleDateFormat basicFormatter = new SimpleDateFormat("dd-MM-yyyy");

        return onlyTimeFormatter.format(dateInMilliSeconds);


    }

    public static String getPrettyDuration(long durationInMilliSeconds){
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(durationInMilliSeconds),
                TimeUnit.MILLISECONDS.toSeconds(durationInMilliSeconds) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(
                                durationInMilliSeconds)));
    }

    /**
     * This method checks if the grouo expiry is in next three days
     * Returns true if it is
     * @param groupExpiryTime
     * @return
     */
    public static boolean isExpiryNear(Long groupExpiryTime){

        if(groupExpiryTime == null || groupExpiryTime == 0){ //group expiry is manual
            return false;
        }
        Date today = new Date();
        long todaysDateInMilliseconds = today.getTime();
        long threeDaysLaterInMilliseconds = todaysDateInMilliseconds + (2L * 1000L * 24L * 60L * 60L);
        return threeDaysLaterInMilliseconds >= groupExpiryTime;
    }
}
