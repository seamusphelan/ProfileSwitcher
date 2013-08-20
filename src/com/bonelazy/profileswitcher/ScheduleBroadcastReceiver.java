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

import java.util.Calendar; 
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduleBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ProfileSwitcher profileSwitcher = new ProfileSwitcher(context);
		//Set the next alarm - do this first in case anything funny happens later
		profileSwitcher.setNextAlarm();


		if ((intent.getAction() != null) && (
				(intent.getAction().equals(android.content.Intent.ACTION_BOOT_COMPLETED )) ||
				(intent.getAction().equals(android.content.Intent.ACTION_MY_PACKAGE_REPLACED)) )) {
			ProfileSwitcher.myLog("Received intent " + intent.getAction() );
			profileSwitcher.startService(); 
			return;
		}

		//check if we are in the middle of a timed profile - timedProfileExires
		//	find profile to switch to 
		//	set setdelayedProfileName
		Calendar timedProfileExires = profileSwitcher.getTimedProfileExires();
		if (timedProfileExires != null && timedProfileExires.after(Calendar.getInstance())) {
			//we are in the middle of a timed profile - 
			//set the profile that we want to revert to
			profileSwitcher.setTimedProfileToRevertTo(profileSwitcher.getCurrentScheduledProfile());			
			profileSwitcher.myToast("When timed profile has finished, profile will be set to " + 
					profileSwitcher.getCurrentScheduledProfile());
				//ProfileSwitcher.getProfileName(profileSwitcher.getCurrentScheduledProfile()));
			return;
		}

		//If we have reached here, then we are doing either 
		// 1. resetting a profile after a timed profile
		// 2. or a normal scheduled change
		String nextProfile = profileSwitcher.getProfileToChangeTo();
		ProfileSwitcher.myLog("In ScheduleBroadcastReceiver - switching to " + nextProfile);
		profileSwitcher.setActiveProfile(nextProfile);
		return;
	}
}
