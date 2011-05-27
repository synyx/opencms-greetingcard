/*
 * This file is part of the Synyx Greetingcard module for OpenCms.
 *
 * Copyright (c) 2007 Synyx GmbH & Co.KG (http://www.synyx.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.synyx.greetingcard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Encapsulates information for current date.
 * @author Florian Hopf, Synyx GmbH & Co. KG
 */
public class DateBean {
    
    private Calendar cal = Calendar.getInstance();
    private long nextMinuteTime = 0;
    private SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmm");
    private SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    
    public static final int START_YEAR = 2006;
    
    /** Creates a new instance of DateBean */
    public DateBean() {
        sdf.setLenient(false);
        format.setLenient(false);
    }

    public static final int getAmountDays(final int searchedMonth, final int searchedYear) {
        int temp = -1;
        switch (searchedMonth) {
            case 1: temp = 31;
            break;
            case 2: temp = getDaysOfFebruary(searchedYear);
            break;
            case 3: temp = 31;
            break;
            case 5: temp = 31;
            break;
            case 7: temp = 31;
            break;
            case 8: temp = 31;
            break;
            case 10: temp = 31;
            break;
            case 12: temp = 31;
            break;
            default: temp = 30;
        }
        return temp;
    }

    /**
     * This method returns the amount of days from february.
     * @return The amount of days from february.
     * @param year the year for that the days in february are asked
     */
    public static final int getDaysOfFebruary(final int year) {
        int days = 28;
        if ((year % 400 == 0) || (year % 4 == 0 && year % 100 != 0)) {
            days = days + 1;
        }
        return days;
    }
    
    /**
     * Returns the current day.
     * @return an int
     */
    public int getCurrentDay() {
        return getCalendar().get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * Returns the current month.
     * @return an int
     */
    public int getCurrentMonth() {
        return getCalendar().get(Calendar.MONTH) + 1;
    }
    
    /**
     * Returns the current year.
     * @return an int
     */
    public int getCurrentYear() {
        return getCalendar().get(Calendar.YEAR);
    }
    
    /**
     * Returns the current minute.
     * @return an int
     */
    public int getCurrentMinute() {
        return getCalendar().get(Calendar.MINUTE);
    }
    
    /**
     * Returns the current hour.
     * @return an int
     */
    public int getCurrentHour() {
        return getCalendar().get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * Returns a List with all the years used for the send-dialog.
     * @return a List of Integers
     */
    public List<Integer> getYearsForSending() {
        List<Integer> years = new ArrayList<Integer>();
        years.add(getCalendar().get(Calendar.YEAR));
        years.add(getCalendar().get(Calendar.YEAR) + 1);
        return years;
    }
    
    /**
     * Returns a List with all the years for statistic.
     * @return a List of Integers
     */
    public List<Integer> getYearsForStatistic() {
        List<Integer> years = new ArrayList();
        for (int i = DateBean.START_YEAR; i <= getCalendar().get(Calendar.YEAR); i++) {
            years.add(i);
        }
        return years;
    }
    
    /**
     * Returns the Calendar for the current date, precise to minute.
     * @return a Calendar object for the current day
     */
    public Calendar getCalendar() {
        if (nextMinuteTime >= System.currentTimeMillis()) {
            Date current = new Date();
            cal.setTime(current);
            // calculate the time for day transition
            cal.add(Calendar.MINUTE, 1);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            nextMinuteTime = cal.getTimeInMillis();
            // set the calendar to current time again
            cal.setTime(current);
        }
        return cal;
    }
    
    /**
     * Parses the given date parts if valid.
     * @return the Date, null if parsing failed
     */
    public Date parseDate(String day, String month, String year, String hour, String minute) {
        Date result = null;
        try {
            result = sdf.parse(day.concat(month).concat(year).concat(hour).concat(minute));
        } catch (ParseException ex) {
            result = null;
        }
        return result;
    }
    
    /**
     * Formats the given date.
     * @return formattedDate
     */
    public String formatDate(Date date) {
        return format.format(date);
    }
    
    /**
     * Formats the given long as date.
     * @return formattedDated
     */
    public String formatDate(long time) {
        return formatDate(new Date(time));
    }
    
    public String formatTwoDigits(int num) {
       if (num < 10) {
           return "0".concat(String.valueOf(num));
       } else {
           return String.valueOf(num);
       }
    }
    
}
