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

import android.content.BroadcastReceiver; 
import android.content.Context;
import android.content.Intent;


public class HeadsetBroadcastReceiver extends BroadcastReceiver{
	// http://developer.android.com/reference/android/content/BroadcastReceiver.html
 
	@Override
	public void onReceive(Context context, Intent intent) {

	    int state = intent.getIntExtra("state", -1);
//	    state - 0 for unplugged, 1 for plugged.
//	    name - Headset type, human readable string
//	    microphone - 1 if headset has a microphone, 0 otherwise
	
		ProfileSwitcher profileSwitcher = new ProfileSwitcher(context);
		ProfileSwitcher.myLog("in HeadsetBroadcastReceiver - onReceive - intent.getAction: " + intent.getAction() + 
				" - isSticky:"+ isInitialStickyBroadcast()  +
				" - state:" + state);
		//profileSwitcher.myToast("headset - In onReceive - intent.getAction: " + intent.getAction() + 
		//		"- isSticky:"+ isInitialStickyBroadcast());
		ProfileSwitcher.myLog("in HeadsetBroadcastReceiver - onReceive - resetOnHeadsetPlug = " + profileSwitcher.resetOnHeadsetPlug() +
							  ", resetOnHeadsetUnplug = " +profileSwitcher.resetOnHeadsetUnplug() );

		ProfileSwitcher.myLog("in HeadsetBroadcastReceiver - onReceive - context.getPackageName() = " + context.getPackageName() );
		
		if (isInitialStickyBroadcast()) {
			return;
		}
		
		if (! profileSwitcher.resetOnHeadsetPlug() && ! profileSwitcher.resetOnHeadsetUnplug() ) {
			ProfileSwitcher.myLog("in HeadsetBroadcastReceiver - stopping service ");
			profileSwitcher.stopService();
			return;
		}
		
		if ( state == 1 && ! profileSwitcher.resetOnHeadsetPlug() ){
			return;
		}else if ( state == 0 &&  ! profileSwitcher.resetOnHeadsetUnplug() ){
			return;
		}
		
		String nextProfileName = profileSwitcher.getProfileToChangeTo();
		ProfileSwitcher.myLog("In HeadsetBroadcastReceiver - switching to " + nextProfileName);
		profileSwitcher.setActiveProfile(profileSwitcher.getActiveProfileName());
		return;
	    
	}
}
