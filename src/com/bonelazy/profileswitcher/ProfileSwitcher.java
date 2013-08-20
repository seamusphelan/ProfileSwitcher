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
import java.util.List;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Profile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;
import android.os.RemoteException;

import android.app.IProfileManager;
import android.app.ProfileManager;



public class ProfileSwitcher {

	private static final String TAG = "ProfileSwitcher";

	private Context context; 
	private String[] profileNames;
	private Boolean debugMode;
	private static Boolean resetOnHeadsetPlug;
	private static Boolean resetOnHeadsetUnplug;
	
	private static IProfileManager sService; 
	private static Boolean inEmulator;

	private String calculatedCurrentScheduledProfile;
	private Calendar calculatedNextScheduledAlarm;
	private Calendar lastCalculationRun; 


	static final int TIMED_ALARM = 1;  //Used for the pending intent requestCode
	static final int REGULAR_ALARM = 2; 


	public ProfileSwitcher(Context context){
		this.context = context;

		SharedPreferences preferences = getMySharedPreferences();	

		debugMode = preferences.getBoolean("debug_mode", false);
		resetOnHeadsetPlug = preferences.getBoolean("reset_on_headset_plug", false);
		resetOnHeadsetUnplug = preferences.getBoolean("reset_on_headset_unplug", false);
		myLog("in ProfileSwitcher - constructor - resetOnHeadsetPlug = " + resetOnHeadsetPlug() +
				", resetOnHeadsetUnplug = " + resetOnHeadsetUnplug() );
		myLog("in ProfileSwitcher - constructor - context.getPackageName() = " + context.getPackageName() );

		// These four lines are to set the 'in_emulator' preference. Necessary for the first time running in the emulator
		//Editor edit = preferences.edit();
		//edit.putBoolean("in_emulator", true);
		//myToast("set in pref: in_emulator = true");
		//edit.commit();

		inEmulator = preferences.getBoolean("in_emulator", false);
		if (inEmulator) {
			//Since I am using the standard google ROM in the emulator, I use dummy 
			//profiles.
			profileNames = new String[]{"Profilename1","Profilename2","Profilename3","Profilename4","Profilename5"};
		} else {
			try{
				sService = ProfileManager.getService();
			}
			catch(NoClassDefFoundError e) {
				myToast("This app only works on a ROM with a ProfileManager (eg CM9 & CM10). You should exit now :-(");
				myLog("*** Unable to get ProfileManager service **** ");
			}
			getAllProfiles();
		}       
	}

	private SharedPreferences getMySharedPreferences(){
		return context.getSharedPreferences("profile_switcher", Context.MODE_MULTI_PROCESS);		
	}
 
	public boolean isDebugEnabled(){
		return debugMode;
	}	

	public void toggleDebugMode(){
		debugMode = ! debugMode;
		SharedPreferences preferences = getMySharedPreferences();	
		Editor edit = preferences.edit();
		edit.putBoolean("debug_mode", debugMode);
		edit.commit();
	}

	public static void myLog(String log) {
		Calendar now = Calendar.getInstance();
		Log.v(TAG, calendarToString(now) + " : " + log);		
	}

	public void myToast(String toast) {
		if (isDebugEnabled()){
			myLog("Sending toast:" + toast);			
		}
		Toast.makeText(context, "ProfileSwitcher: " + toast, Toast.LENGTH_LONG).show();
	}

	public static String calendarToString(Calendar cal){
		if (cal == null){
			return "<never>";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd E HH:mm");
		return formatter.format(cal.getTime());		
	}

	public static String calendarToEHHmm(Calendar cal){
		if (cal == null){
			return "<never>";
		}
		SimpleDateFormat formatter = new SimpleDateFormat("E HH:mm");
		return formatter.format(cal.getTime());		
	}

	public String[] getAllProfiles() {
		if (inEmulator){
			return profileNames;
		} else if ( sService == null) {
			return new String[]{"no profile manger found"};
		} else {
			if (profileNames == null && sService != null) {
				try {
					Profile[] allProfiles = sService.getProfiles();
					profileNames = new String[allProfiles.length];
					int i = 0;
					for (Profile p : allProfiles){
						profileNames[i++] = p.getName();
					}
				} catch (RemoteException e) {
					Log.e(TAG, e.getLocalizedMessage(), e);
				}		
			}		
			return profileNames;
		}
	}


	public String getActiveProfileName() {
		if (inEmulator){
			SharedPreferences preferences = getMySharedPreferences();	
			return preferences.getString("testmode_activeProfileName", "<no active profile>");
		} else if ( sService == null) {
			return "<error - no profile manager>";
		} else {
			try {
				return sService.getActiveProfile().getName();
			} catch (RemoteException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				return "<error getting active profile name>";
			}
		}
	}

	public boolean setActiveProfile(String profileName) {
		Calendar timedProfileExires = getTimedProfileExires();
		if (timedProfileExires != null){
			if (timedProfileExires.before(Calendar.getInstance())) {
				//This is the first time setting a profile after a time profile
				//We will be setting the profile to the original profile before the timed profile 
				//started (or a scheduled profile change that happened in the middle of timed profile)
				//Delete the timed profile settings now, so that we don't reset to this next time
				deleteTimedProfile();
			}
		}		

		if (inEmulator){
			SharedPreferences preferences = getMySharedPreferences();	
			Editor edit = preferences.edit();
			edit.putString("testmode_activeProfileName", profileName);
			edit.commit();
			myToast("Profile changed to " + profileName);
			return true;
		} 
		else {

			if (profileName == null) {
				myToast("Error setting profile to 'null'");
				return false;
			} else if ( sService == null) {
				myToast("Error setting profile - no profile manager found");
				return false;
			}
			try {
				sService.setActiveProfileByName(profileName);
				myToast("Profile changed to " + profileName);
				return true;
			} catch (RemoteException e) {
				Log.e(TAG, e.getLocalizedMessage(), e);
				myToast("Error setting profile:" + e.getLocalizedMessage() );
				return false;
			}
		}
	}

	public void setNextAlarm(){
		Calendar nextAlarm = getNextScheduledAlarm();
		if (nextAlarm == null){
			myToast("Error finding nextAlarm - not setting any");
		} else {
			myLog("Setting next alarm for " + calendarToEHHmm(nextAlarm));
			setAlarm(nextAlarm, REGULAR_ALARM);
		}
	}

	public boolean setAlarm(Calendar alarmTime, int alarmType){
		Intent intent = new Intent(context, ScheduleBroadcastReceiver.class);
		//intent.putExtra("com.bonelazy.profileswitcher.test1", "test1" + Calendar.getInstance().getTime().toString());
		//PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 234324243, intent, 0);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), alarmType, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);

		//We shouldn't need to do this 
		//not doing this to see if it all works...
		//startService();

		//		boolean alarmUp = (PendingIntent.getBroadcast(context.getApplicationContext(), 0, new Intent(context, ScheduleBroadcastReceiver.class), 
		//				PendingIntent.FLAG_NO_CREATE) != null);
		////		boolean alarmUp = (PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, 
		////				PendingIntent.FLAG_NO_CREATE) != null);
		//		if (alarmUp)
		//		{
		//		    Log.d(TAG, "Alarm is already active");
		//		}else
		//		{
		//			Log.d(TAG, "Alarm is NOT active");
		//		}
		return true;
	}

	public void startService(){
		if ( resetOnHeadsetPlug() || resetOnHeadsetUnplug() ) {
			Intent service = new Intent(context, HeadsetService.class);
			context.startService(service);		
		}
	}

	public void stopService(){
		Intent service = new Intent(context, HeadsetService.class);
		context.stopService(service);		
	}

	public void startProfileManager(){
		//Intent service = new Intent(context, ProfileManager.class);
		//context.startService(service);		
	}

	public boolean resetOnHeadsetPlug(){
		return resetOnHeadsetPlug;
	}	

	public void toggleResetOnHeadsetPlug(){
		resetOnHeadsetPlug = ! resetOnHeadsetPlug;
		SharedPreferences preferences = getMySharedPreferences();	
		Editor edit = preferences.edit();
		edit.putBoolean("reset_on_headset_plug", resetOnHeadsetPlug);
		edit.commit();
		//		//since the service is running on a different process, we need to restart it so 
		//		//that it picks these changes!!!
		//		stopService(); 
		startService();
	}

	public boolean resetOnHeadsetUnplug(){
		return resetOnHeadsetUnplug;
	}	

	public void toggleResetOnHeadsetUnplug(){
		resetOnHeadsetUnplug = ! resetOnHeadsetUnplug;
		SharedPreferences preferences = getMySharedPreferences();	
		Editor edit = preferences.edit();
		edit.putBoolean("reset_on_headset_unplug", resetOnHeadsetUnplug);
		edit.commit();
		//		//since the service is running on a different process, we need to restart it so 
		//		//that it picks these changes!!!
		//		stopService(); 
		startService();
	}


	public void setTimedProfile(String timedProfileName, Calendar timedProfileExires){
		SharedPreferences preferences = getMySharedPreferences();	

		Editor edit = preferences.edit();
		edit.putString("timedProfileName", timedProfileName);
		edit.putLong("timedProfileExires", timedProfileExires.getTimeInMillis());
		//If timedProfileToRevertToName already exists, then we are setting a timed profile 
		//while already in a timed profile.  In this scenario, we would not want to 
		//overwrite the timedProfileToRevertToName. 
		if (getTimedProfileToRevertTo() == null){
			edit.putString("timedProfileToRevertToName", getActiveProfileName() );					
			myLog("set in pref: timedProfileToRevertToName = " + getTimedProfileToRevertTo());
		}
		edit.commit();

		myLog("set in pref: timedProfileName = " + timedProfileName);
		myLog("set in pref: timedProfileExires = " + calendarToString(timedProfileExires));

		setAlarm(timedProfileExires, TIMED_ALARM);
	}

	public void deleteTimedProfile(){
		SharedPreferences preferences = getMySharedPreferences();	
		Editor edit = preferences.edit();

		edit.remove("timedProfileName");
		edit.remove("timedProfileToRevertToName");		
		edit.remove("timedProfileExires");
		edit.commit();

		myLog("in deleteTimedProfile - removed timedProfileName, timedProfileToRevertToName, timedProfileExires");
	}


	public String getTimedProfileName(){
		//This is what the user set the profile to!
		SharedPreferences preferences = getMySharedPreferences();	
		String timedProfileName = preferences.getString("timedProfileName", null);
		myLog("got from pref: timedProfileName = " + timedProfileName);
		return timedProfileName;
	}


	public Calendar getTimedProfileExires(){
		//This is the expiry time for a timed profile
		SharedPreferences preferences = getMySharedPreferences();	
		long timedProfileExiresLong = preferences.getLong("timedProfileExires", 0);
		if (timedProfileExiresLong == 0 ){ 
			return null;
		}
		Calendar timedProfileExires = Calendar.getInstance();
		timedProfileExires.setTimeInMillis(timedProfileExiresLong);
		myLog("got from pref: timedProfileExires = " + calendarToString(timedProfileExires));
		//if (timedProfileExires.before(Calendar.getInstance())) {
		//	//this is an old timed profile
		//	return null;
		//}
		return timedProfileExires;
	}

	public String getTimedProfileToRevertTo(){
		//This was the profile before the user switched
		SharedPreferences preferences = getMySharedPreferences();	
		String timedProfileToRevertToName = preferences.getString("timedProfileToRevertToName", null);
		myLog("got from pref: timedProfileToRevertToName = " + timedProfileToRevertToName);
		return timedProfileToRevertToName;
	}
	
	public void setTimedProfileToRevertTo(String timedProfileToRevertTo){
		SharedPreferences preferences = getMySharedPreferences();	

		Editor edit = preferences.edit();
		edit.putString("timedProfileToRevertToName", timedProfileToRevertTo );					
		edit.commit();
		myLog("set in pref: timedProfileToRevertToName = " + timedProfileToRevertTo);
	}


	public String getProfileToChangeTo(){
		Calendar timedProfileExires = getTimedProfileExires();
		if (timedProfileExires != null) {
			if (timedProfileExires.after(Calendar.getInstance())) {
				return getTimedProfileName();
			} else {
				return getTimedProfileToRevertTo();
			}
		}

		return getCurrentScheduledProfile();
	}

	public String getCurrentScheduledProfile(){
		//get the current profile as per the schedule
		calculateSchedule();
		return calculatedCurrentScheduledProfile;
	}

	public Calendar getNextScheduledAlarm(){
		//get the next alarm as per the schedule
		calculateSchedule();
		return calculatedNextScheduledAlarm;
	}

	public void signalPossibleChangesMade(){
		//(I'm not a fan of how this works .... but it works)
		//Force a recalculation.  Important if we think that a change has been made.
		lastCalculationRun = null;
	}
	
	private void calculateSchedule(){
		// This methods set the following variables...
		// 		calculatedCurrentScheduledProfile 
		//		calculatedNextScheduledAlarm 
		//		lastCalculationRun 
		//  NB - This method does not consider 'timed profiles'
		//       In hindsight it should... 
		
		ScheduleDataSource datasource = new ScheduleDataSource(context);
		datasource.open();
		List<Schedule> allSchedules = datasource.getAllSchedules();        
		datasource.close();

		Calendar now = Calendar.getInstance();
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MILLISECOND, 0);
		if ( now.equals(lastCalculationRun)) {
			if (isDebugEnabled()) {
				myLog("SPSP reusing existing calculation"			
						+ calendarToString(now)
						+ calendarToString(lastCalculationRun));
			}
			//We already have calculated the schedule for this current minute. No need to do it again and again....
			return;
		}
		
		if (isDebugEnabled()) {
			myLog("SPSP performing calculation" 
					+ calendarToString(now)
					+ calendarToString(lastCalculationRun));
		}
		Schedule nextSchedule = null;  //this is the next schedule that will be set at the next alarm
		Calendar nextScheduleAlarm = null;
		Schedule currentSchedule = null;  //this is the current schedule to be set (its start time may be now or before now)
		Calendar currentScheduleAlarm = null; //this will be a time in the past or now
		for (Schedule schedule :  allSchedules){
			Calendar nextAlarm = schedule.getNextAlarm();
			Calendar lastAlarm = schedule.getLastAlarm();
			if (lastAlarm == null | nextAlarm == null) continue;
			if (nextSchedule == null) {
				nextSchedule = schedule;
				nextScheduleAlarm = nextAlarm;
			}
			if (nextAlarm.before(nextScheduleAlarm)) { 
				nextSchedule = schedule;
				nextScheduleAlarm = nextAlarm;
			}

			if (currentSchedule == null) {
				currentSchedule = schedule;
				currentScheduleAlarm = lastAlarm;
			}
			if (lastAlarm.after(currentScheduleAlarm)) {
				currentSchedule = schedule;
				currentScheduleAlarm = lastAlarm;
			}

		}		
		if (currentSchedule == null | nextScheduleAlarm == null){
			calculatedCurrentScheduledProfile = null;
			calculatedNextScheduledAlarm = null;
		} else {
			calculatedCurrentScheduledProfile = currentSchedule.getProfile();
			calculatedNextScheduledAlarm = nextScheduleAlarm;
		}
		lastCalculationRun = now;
	}
}

//https://github.com/CyanogenMod/android_frameworks_base/blob/ics/core/java/android/app/Profile.java


//#pwd
///n71j/temp/cm9/cm9_github/android/system/frameworks/base/core/java/android/app


//#grep -i public ProfileManager.java 
//public class ProfileManager {
//    static public IProfileManager getService() {
//    public void setActiveProfile(String profileName) {
//    public void setActiveProfile(UUID profileUuid) {
//    public Profile getActiveProfile() {
//    public void addProfile(Profile profile) {
//    public void removeProfile(Profile profile) {
//    public void updateProfile(Profile profile) {
//    public Profile getProfile(String profileName) {
//    public Profile getProfile(UUID profileUuid) {
//    public String[] getProfileNames() {
//    public Profile[] getProfiles() {
//    public boolean profileExists(String profileName) {
//    public boolean profileExists(UUID profileUuid) {
//    public boolean notificationGroupExists(String notificationGroupName) {
//    public NotificationGroup[] getNotificationGroups() {
//    public void addNotificationGroup(NotificationGroup group) {
//    public void removeNotificationGroup(NotificationGroup group) {
//    public void updateNotificationGroup(NotificationGroup group) {
//    public NotificationGroup getNotificationGroupForPackage(String pkg) {
//    public NotificationGroup getNotificationGroup(UUID uuid) {
//    public ProfileGroup getActiveProfileGroup(String packageName) {
//    public void resetAll() {
