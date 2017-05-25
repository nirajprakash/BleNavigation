package com.intugine.ble.beacon.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.intugine.ble.beacon.util.LogUtils.makeLogTag;

/**
 * Created by niraj on 05-05-2017.
 */

public class UtilDateTime {
    private static final String TAG = makeLogTag(UtilDateTime.class);

    public static long[] getBookingRange() {
        long[] dateRange = new long[2];
        dateRange[0] = Calendar.getInstance().getTimeInMillis();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        /*int hour=calendar.get(Calendar.HOUR_OF_DAY);
        int min=calendar.get(Calendar.MINUTE);
        int sec=calendar.get(Calendar.SECOND);*/
        long range = new GregorianCalendar(0, 0, 14, 0, 0, 0).getTimeInMillis();
        //GregorianCalendar(0, 0, 0, mLatestHour, mLatestMin).getTime();
        calendar.add(Calendar.DATE, 14);
        dateRange[1] = calendar.getTimeInMillis();//[0]+range;
        //dateRange[1] = dateRange[0]+range;
        return dateRange;
    }

    public static long getExpireDateInLong(int second){
        Calendar calendar = Calendar.getInstance();
        int secondOffsetted = second- 60;
        int mins =  secondOffsetted/60;
        int hours = mins/60;
        int days = hours/24;

        mins = mins%60;
        hours =hours%24;

        calendar.add(Calendar.DAY_OF_YEAR, days);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        calendar.add(Calendar.MINUTE, mins);
        return calendar.getTimeInMillis();
    }

    public static int[] getHourAndMin(int intTime) {
        int[] hourMin = new int[2];
        hourMin[1] = intTime % 100;
        hourMin[0] = intTime / 100;
        return hourMin;
    }

    public static int[] getHourAndMinFromOffset(int hour, int min, int offsetHour, int offsetMin){
        int [] hourMin = new int[2];
        Calendar calendar = new GregorianCalendar(0,0,0,hour,min);
        calendar.add(Calendar.HOUR_OF_DAY, offsetHour);
        calendar.add(Calendar.MINUTE, offsetMin);
        hourMin[0] = calendar.get(Calendar.HOUR_OF_DAY);
        hourMin[1] = calendar.get(Calendar.MINUTE);

        return hourMin;
    }

    public static int[] getDateInt(long dateInMillis) {
        //Date date = new Date(dateInMillis);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateInMillis);
        int[] datesInt = new int[3];
        datesInt[0] = calendar.get(Calendar.YEAR);
        datesInt[1] = calendar.get(Calendar.MONTH);
        datesInt[2] = calendar.get(Calendar.DATE);
        return datesInt;
    }

    public static String getDateFormatted(int pYear, int pMonth, int pDay, int pHour, int pMin) {
        Date date = new GregorianCalendar(pYear, pMonth, pDay, pHour, pMin).getTime();
        return new SimpleDateFormat("dd-MMM-yyyy").format(date).toString();
    }

    public static String getTimeFormatted(int pHour, int pMin) {
        Date date = new GregorianCalendar(0, 0, 0, pHour, pMin).getTime();
        return new SimpleDateFormat("hh:mm a").format(date).toString();
    }

    public static String getTimeFormatted(int timeInt) {
        int[] hourMin = getHourAndMin(timeInt);
        return getTimeFormatted(hourMin[0], hourMin[1]);
    }

    public static String getTimeFormatted(long time) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return dateFormat.format(calendar.getTime());
    }


    public static String getTimeFormattedMillis(long time) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss:SSS");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return dateFormat.format(calendar.getTime());
    }

    public static String getTimingFormatted(int timeStart, int timeEnd) {

        String strStartTime = "";
        String strEndTime = "";

        //LOGV(TAG, "timeFormatted: ");

        strStartTime = getTimeFormatted(timeStart);
        //LOGV(TAG, "timeStart: " + strStartTime);

        strEndTime = getTimeFormatted(timeEnd);
        //LOGV(TAG, "timeEnd: " + strEndTime);

        return strStartTime + " - " + strEndTime;
    }

    public static long getDateInMillis(int pYear, int pMonth, int pDay, int pHour, int pMin) {
        return  new GregorianCalendar(pYear, pMonth, pDay, pHour, pMin).getTimeInMillis();
    }

    public static String getDateFormatted(long date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return dateFormat.format(calendar.getTime());
    }

    public static String getDateSimpleFormatted(long date) {
        DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return dateFormat.format(calendar.getTime());
    }

    public static String getFirstTimingFormatted(int timeStart) {

        return getTimeFormatted(timeStart);
    }
}
