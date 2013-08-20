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

import java.util.Arrays; 
import java.util.Calendar;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;


public class TimedProfileFragment extends DialogFragment implements
						OnClickListener {

	private ProfileSwitcher profileSwitcher;
	private String[] profileNames;
	private Spinner profileNameSpinnerView;
	private TimePicker timePickerView;
	private Button buttonSetTimedProfile;
	private Button button0030;
	private Button button0100;
	private Button button0200;
	private Button button0300;
	private Button buttonCancel;

	static TimedProfileFragment newInstance(Context myContext) {
		return new TimedProfileFragment();
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().setTitle("Set timed profile");

		profileSwitcher = new ProfileSwitcher(this.getActivity()
				.getBaseContext());

		View v = inflater.inflate(R.layout.timed_profile, container, false);

		profileNames = profileSwitcher.getAllProfiles();
		ProfileSwitcher.myLog("all profile Names - " + profileNames);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this
				.getActivity().getBaseContext(),
				android.R.layout.simple_spinner_item, profileNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		profileNameSpinnerView = (Spinner) v.findViewById(R.id.profileNameSpinner);
		profileNameSpinnerView.setAdapter(adapter);

		// find 'Silent'
		int silentIndex = Arrays.asList(profileNames).indexOf("Silent");
		if ( silentIndex > 0 ) {
			profileNameSpinnerView.setSelection(silentIndex);
		}

		// timePickerView
		buttonSetTimedProfile = (Button) v.findViewById(R.id.buttonSetTimedProfile);
		timePickerView = (TimePicker) v.findViewById(R.id.timePicker);
		button0030 = (Button) v.findViewById(R.id.button0030);
		button0100 = (Button) v.findViewById(R.id.button0100);
		button0200 = (Button) v.findViewById(R.id.button0200);
		button0300 = (Button) v.findViewById(R.id.button0300);
		buttonCancel = (Button) v.findViewById(R.id.buttonCancel);
		timePickerView.setIs24HourView(true);
		timePickerView.setCurrentHour(0);
		timePickerView.setCurrentMinute(30);
		buttonSetTimedProfile.setOnClickListener(this);
		button0030.setOnClickListener(this);
		button0100.setOnClickListener(this);
		button0200.setOnClickListener(this);
		button0300.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
		return v;
	}

	public void onClick(View v) {

		int minutesToAdd = 0;
		Calendar alarmTime = Calendar.getInstance();
		alarmTime.set(Calendar.SECOND, 0);
		alarmTime.set(Calendar.MILLISECOND, 0);

		switch (v.getId()) {
		case R.id.buttonSetTimedProfile:
			alarmTime.add(Calendar.HOUR_OF_DAY, timePickerView.getCurrentHour());
			alarmTime.add(Calendar.MINUTE, timePickerView.getCurrentMinute());
			break;
		case R.id.button0030:
			minutesToAdd = 30;
			break;
		case R.id.button0100: 
			minutesToAdd = 1 * 60;
			break;
		case R.id.button0200:
			minutesToAdd = 2 * 60;
			break;
		case R.id.button0300:
			minutesToAdd = 3 * 60;
			break;
		case R.id.buttonCancel:
			dismiss();
			return;
		default:
			dismiss();
			return;
		}


		if ( minutesToAdd > 0 ) {
			Calendar duration = Calendar.getInstance();
			duration.set(Calendar.HOUR_OF_DAY, timePickerView.getCurrentHour());
			duration.set(Calendar.MINUTE, timePickerView.getCurrentMinute());
			duration.add(Calendar.MINUTE, minutesToAdd);
			timePickerView.setCurrentHour(duration.get(Calendar.HOUR_OF_DAY));
			timePickerView.setCurrentMinute(duration.get(Calendar.MINUTE));
			return;
		}


		//If we get to here, then we are setting the timed profile....
		ProfileSwitcher.myLog("setting timed profile "
				+ ProfileSwitcher.calendarToString(alarmTime));
		profileSwitcher.setTimedProfile(profileNames[profileNameSpinnerView.getSelectedItemPosition()],
				alarmTime);
		profileSwitcher.setActiveProfile(profileNames[profileNameSpinnerView.getSelectedItemPosition()]);
		ProfileSwitcherActivity myActivity = (ProfileSwitcherActivity) getActivity();
		myActivity.showAllSchedules();
		dismiss();
	}
}
