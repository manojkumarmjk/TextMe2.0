package com.bymjk.txtme.Components;

import java.util.Calendar;

public class TimeUtils {

    public static String getDateTimeAndDayInStr(long timeStamp){
        String lastMsgTimeStr = "";
        long currentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTimeMillis);
        int currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTimeInMillis(timeStamp);
        int yourDayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        if (yourDayOfYear == currentDayOfYear) {
            // the time is today
            lastMsgTimeStr = HelperFunctions.getDateTime(timeStamp,1);
        } else if (yourDayOfYear == currentDayOfYear - 1) {
            // the time is yesterday
            lastMsgTimeStr = "Yesterday";
        } else {
            lastMsgTimeStr = HelperFunctions.getDateTime(timeStamp,2);
        }
        return lastMsgTimeStr;
    }
}
