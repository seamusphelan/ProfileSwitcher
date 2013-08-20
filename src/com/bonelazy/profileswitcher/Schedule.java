/*
 * Copyright (C) 2012-2013 Seamus Phelan <SeamusPhelan@gmail.com>
 *
 * This file is part of ProfileSwitcher.
 *
 * ProfileSwitcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ProfileSwitcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ProfileSwitcher.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.bonelazy.profileswitcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.Context;

public class Schedule {
	@SuppressWarnings("unused")
	private static final String TAG = "Schedule";

	private long id;
	private String profileName;
	private boolean enabled;
	private boolean dow_mon;
	private boolean dow_tue;
	private boolean dow_wed;
	private boolean dow_thu;
	private boolean dow_fri;
	private boolean dow_sat;
	private boolean dow_sun;
	private Calendar time;
	
	public Schedule() {
		time = Calendar.getInstance();
		profileName = "<pick a profile>";	
		dow_mon = dow_tue = dow_wed = dow_thu = dow_fri = dow_sat = dow_sun = true;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getProfile() {
		return profileName;
	}

	public void setProfile(String profileName) {
		this.profileName = profileName;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getEnableAsInt() {
		return  (enabled)? 1 : 0 ;
	}

	public void setEnable(boolean enabled) {
		this.enabled = enabled;
	}

	public void setEnableFromInt(int enabledInt) {
		this.enabled = ( enabledInt > 0 );
	}

	public Calendar getTime() {
		return time;
	}

	public int getTimeAsInt() {
		return time.get(Calendar.HOUR_OF_DAY) * 60 + time.get(Calendar.MINUTE);
	}

	public void setTime(Calendar time) {
		this.time = time;
	}

	public void setTimeHour(int hour) {
		time.set(Calendar.HOUR_OF_DAY, hour);
	}
	public void setTimeMinute(int minute) {
		time.set(Calendar.MINUTE, minute);
	}

	public String getTimeAsString() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		return formatter.format(time.getTime()); 
	}
	

	public void setTimeFromInt(int timeAsInt) {
		if ( this.time == null ) {
			this.time = Calendar.getInstance();	
		}
		this.time.set(Calendar.HOUR_OF_DAY, (int) timeAsInt/60);
		this.time.set(Calendar.MINUTE, timeAsInt % 60);			
	}
	
	public boolean isDowActive(int dow){
		switch (dow) {
		case Calendar.MONDAY:
			return dow_mon;
		case Calendar.TUESDAY:
			return dow_tue;
		case Calendar.WEDNESDAY:
			return dow_wed;
		case Calendar.THURSDAY:
			return dow_thu;
		case Calendar.FRIDAY:
			return dow_fri;
		case Calendar.SATURDAY:
			return dow_sat;
		case Calendar.SUNDAY:
			return dow_sun;
		default:
			return false;
		}
	}
			
	public int getDowAsInt(int dow) {		
		return  (isDowActive(dow))? 1 : 0 ;
	}
	
	public String getDaysOfWeek(Context context){
		String daysOfWeek = "";
		if (dow_mon) daysOfWeek += context.getString(R.string.dow_short_mon) + " ";
		if (dow_tue) daysOfWeek += context.getString(R.string.dow_short_tue) + " ";
		if (dow_wed) daysOfWeek += context.getString(R.string.dow_short_wed) + " ";
		if (dow_thu) daysOfWeek += context.getString(R.string.dow_short_thu) + " ";
		if (dow_fri) daysOfWeek += context.getString(R.string.dow_short_fri) + " ";
		if (dow_sat) daysOfWeek += context.getString(R.string.dow_short_sat) + " ";
		if (dow_sun) daysOfWeek += context.getString(R.string.dow_short_sun) + " ";
		return daysOfWeek;		
	}
	
	public void setDow(int dow, boolean active){
		switch (dow) {
		case Calendar.MONDAY:
			dow_mon = active;
			break;
		case Calendar.TUESDAY:
			dow_tue = active;
			break;
		case Calendar.WEDNESDAY:
			dow_wed = active;
			break;
		case Calendar.THURSDAY:
			dow_thu = active;
			break;
		case Calendar.FRIDAY:
			dow_fri = active;
			break;
		case Calendar.SATURDAY:
			dow_sat = active;
			break;
		case Calendar.SUNDAY:
			dow_sun = active;
			break;
		default:
			break;
		}
	}

	public void setDowFromInt(int dow, int activeInt){
		setDow(dow, ( activeInt > 0 ));
	}

	public Calendar getNextAlarm(){
		//get the next date/time this schedule will be set
		if (enabled == false) return null;

		Calendar now = Calendar.getInstance();
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		//set the date to be today for 'time' - this allows use to do comparisons
		time.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
		time.set(Calendar.SECOND, 0);  // ensure the seconds and milliseconds are zero
		time.set(Calendar.MILLISECOND, 0);

		Calendar nextAlarm = null; // = (Calendar) now.clone();
		Calendar dowCal;
		for (int i = 1; i <= 7; i++ ){
			if ( ! isDowActive(i) ) continue;
			dowCal = (Calendar) time.clone();
			dowCal.set(Calendar.DAY_OF_WEEK, i);			
			if (! dowCal.after(now)) { //not after ==> before or equals to  
				dowCal.add(Calendar.DAY_OF_MONTH, 7);
			}
			if (nextAlarm == null) nextAlarm = dowCal;
			if (dowCal.before(nextAlarm)) nextAlarm = dowCal;
		}		
		//ProfileSwitcher.myLog("nextAlarm for " + profileName + " @ " + ProfileSwitcher.calendarToString(nextAlarm));
		return nextAlarm;
	}

	
	public Calendar getLastAlarm(){
		//get the latest date/time this schedule would have been set
		if (enabled == false) return null;

		Calendar now = Calendar.getInstance();
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		//set the date to be today for 'time' - this allows use to do comparisons
		time.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
		time.set(Calendar.SECOND, 0);  // ensure the seconds and milliseconds are zero
		time.set(Calendar.MILLISECOND, 0);
		
		//ProfileSwitcher.myLog("getLastAlarm:current time is " + ProfileSwitcher.calendarToString(now)+ " " + now.getTimeInMillis());
		
		Calendar lastAlarm = null; // = (Calendar) now.clone();
		Calendar dowCal;
		for (int i = 1; i <= 7; i++ ){
			if ( ! isDowActive(i) ) continue;
			dowCal = (Calendar) time.clone();
			dowCal.set(Calendar.DAY_OF_WEEK, i);			
			//ProfileSwitcher.myLog("lastAlarm - (before dow check) dowCal = " + ProfileSwitcher.calendarToString(dowCal) + " " + dowCal.getTimeInMillis());
			if (dowCal.after(now)) { 
				dowCal.add(Calendar.DAY_OF_MONTH, -7);
			}
			//ProfileSwitcher.myLog("lastAlarm - (after  dow check) dowCal = " + ProfileSwitcher.calendarToString(dowCal) + " " + dowCal.getTimeInMillis());
			if (lastAlarm == null) lastAlarm = dowCal;
			if (dowCal.after(lastAlarm)) lastAlarm = dowCal;
		}		
		//ProfileSwitcher.myLog("lastAlarm for " + profileName + " @ " + ProfileSwitcher.calendarToString(lastAlarm));
		return lastAlarm;
	}
	
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return "ScheduleId:" + id 
				+ " - ProfileName:" + profileName 				
				+ " - Enabled:" + enabled
				+ " - Time: " + getTimeAsString(); 
	
	}
}
