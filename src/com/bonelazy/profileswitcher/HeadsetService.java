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

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;


/* from https://groups.google.com/d/msg/android-developers/bQ3ambT0LI8/0fS0bUQ-w6kJ
 * from http://stackoverflow.com/questions/4202046/which-permission-required-in-order-to-get-action-headset-plug-inside-broadcast-r
 * for a service to receive ACTION_HEADSET_PLUG, it must implement a 
 * ACTION_HEADSET_PLUG cannot be setup in the manifest - it must be setup 
 * in code by a receiver
 */
public class HeadsetService extends Service{


	private int counter;
	private HeadsetBroadcastReceiver headsetBroadcastReceiver;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
        super.onCreate();
        
		counter++ ;
		ProfileSwitcher.myLog("in HeadsetService - onCreate - counter = " + counter);

		headsetBroadcastReceiver = new HeadsetBroadcastReceiver();
		registerReceiver(headsetBroadcastReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
//		registerReceiver(headsetBroadcastReceiver, new IntentFilter(Intent.ACTION_CALL));
//		registerReceiver(headsetBroadcastReceiver, new IntentFilter(Intent.ACTION_CALL_BUTTON));
//		registerReceiver(headsetBroadcastReceiver, new IntentFilter(Intent.ACTION_CAMERA_BUTTON));
//		Intent.ACTION_SCREEN_OFF  SMS_RECEIVED 
		
		
//		registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
//		registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG), broadcastPermission, scheduler);
		
		//context.registerReceiver(new ScheduleBroadcastReceiver(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		
//		return Service.START_NOT_STICKY;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
       
		counter++ ;
		ProfileSwitcher.myLog("in HeadsetService - onStartCommand - counter = " + counter);

//		registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
//		registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG), broadcastPermission, scheduler);
		
		//context.registerReceiver(new ScheduleBroadcastReceiver(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		
		return Service.START_STICKY;
	}


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(headsetBroadcastReceiver != null)
            {
                unregisterReceiver(headsetBroadcastReceiver);
        }
    }
}
