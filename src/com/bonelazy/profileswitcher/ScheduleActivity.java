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
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.app.TimePickerDialog;


public class ScheduleActivity extends Activity {
	private static final String TAG = "ScheduleActivity";

	public static final String EXTRAS_SCHEDULE_ID = "schedule_id";
	private final int MISSING_EXTRAS = -1;
	private ScheduleDataSource datasource;
	private ProfileSwitcher profileSwitcher;

	private Schedule schedule;
	private Spinner profileNameSpinnerView;
	private TextView scheduleTimeView;
	private CheckBox enabledView;
	private String[] profileNames;

	private CheckBox dowMonView;
	private CheckBox dowTueView;
	private CheckBox dowWedView;
	private CheckBox dowThuView;
	private CheckBox dowFriView;
	private CheckBox dowSatView;
	private CheckBox dowSunView;


	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Log.v(TAG, "Starting schedule create/change");
		setContentView(R.layout.schedule_layout);
		datasource = new ScheduleDataSource(this);
		profileSwitcher = new ProfileSwitcher(this);
		datasource.open();

		profileNameSpinnerView = (Spinner) findViewById(R.id.profileNameSpinner);
		enabledView = (CheckBox) findViewById(R.id.enabled);
		//profileNameTextView = (TextView) findViewById(R.id.profileNameText);
		//timePickerView = (TimePicker) findViewById(R.id.timePicker);
		scheduleTimeView = (TextView) findViewById(R.id.scheduleTime);
		//timeView = (EditText) findViewById(R.id.time);
		dowMonView = (CheckBox) findViewById(R.id.dow_mon);
		dowTueView = (CheckBox) findViewById(R.id.dow_tue);
		dowWedView = (CheckBox) findViewById(R.id.dow_wed);
		dowThuView = (CheckBox) findViewById(R.id.dow_thu);
		dowFriView = (CheckBox) findViewById(R.id.dow_fri);
		dowSatView = (CheckBox) findViewById(R.id.dow_sat);
		dowSunView = (CheckBox) findViewById(R.id.dow_sun);

		//		mCategory = (Spinner) findViewById(R.id.category);
		//		mTitleText = (EditText) findViewById(R.id.todo_edit_summary);
		//		mBodyText = (EditText) findViewById(R.id.todo_edit_description);
		//		Button confirmButton = (Button) findViewById(R.id.todo_edit_button);


//		InputMethodManager imm = (InputMethodManager)getSystemService(
//			      Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);


		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			long scheduleId = extras.getLong(EXTRAS_SCHEDULE_ID, MISSING_EXTRAS);
			Log.v(TAG,"found in Extras - scheduleId = " + scheduleId);
			if (scheduleId != MISSING_EXTRAS) {
				schedule = datasource.getSchedule(scheduleId);
				Log.v(TAG,"maintaining schedule:" + schedule.toString());
			}
		}
		if (schedule == null) {
			Log.v(TAG,"Couldn't find schedule - creating new one");
			schedule = new Schedule();
		}
		fillData();

		//		// Check from the saved Instance
		//		todoUri = (bundle == null) ? null : (Uri) bundle
		//				.getParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE);

		// Or passed from the other activity

		//		confirmButton.setOnClickListener(new View.OnClickListener() {
		//			public void onClick(View view) {
		//				if (TextUtils.isEmpty(mTitleText.getText().toString())) {
		//					makeToast();
		//				} else {
		//					setResult(RESULT_OK);
		//					finish();
		//				}
		//			}
		//
		//		});
	}


	public void showTimePickerDialog(View v) {
		//	    DialogFragment newFragment = new TimePickerFragment();
		//	    newFragment.show(getSupportFragmentManager(), "timePicker");
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
//			final Calendar c = Calendar.getInstance();
			int hour = schedule.getTime().get(Calendar.HOUR_OF_DAY);
			int minute = schedule.getTime().get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			schedule.setTimeHour(hourOfDay);
			schedule.setTimeMinute(minute);
			scheduleTimeView.setText(schedule.getTimeAsString());
		}
	}	

	private void fillData() {

		//		String[] projection = { TodoTable.COLUMN_SUMMARY,
		//				TodoTable.COLUMN_DESCRIPTION, TodoTable.COLUMN_CATEGORY };
		//		Cursor cursor = getContentResolver().query(uri, projection, null, null,
		//				null);

		Log.v(TAG,"filling spinner");
//		Object[] profiles = profileSwitcher.getAllProfiles();
//		profileUuid = (List<String>) profiles[0];
//		profileNames = (List<String>) profiles[1];
		profileNames = profileSwitcher.getAllProfiles();

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, profileNames);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		profileNameSpinnerView.setAdapter(adapter);
		profileNameSpinnerView.setSelection(Arrays.asList(profileNames).indexOf(schedule.getProfile()));
	
		
		enabledView.setChecked(schedule.isEnabled());
		scheduleTimeView.setText(schedule.getTimeAsString());
		dowMonView.setChecked(schedule.isDowActive(Calendar.MONDAY));
		dowTueView.setChecked(schedule.isDowActive(Calendar.TUESDAY));
		dowWedView.setChecked(schedule.isDowActive(Calendar.WEDNESDAY));
		dowThuView.setChecked(schedule.isDowActive(Calendar.THURSDAY));
		dowFriView.setChecked(schedule.isDowActive(Calendar.FRIDAY));
		dowSatView.setChecked(schedule.isDowActive(Calendar.SATURDAY));
		dowSunView.setChecked(schedule.isDowActive(Calendar.SUNDAY));
	}

	private void getDataFromView() {

		schedule.setProfile(profileNames[profileNameSpinnerView.getSelectedItemPosition()]);
		schedule.setEnable(enabledView.isChecked());

		//		String category = (String) mCategory.getSelectedItem();
		//schedule.setProfileName(profileNameView.getText());
		//		schedule.setTimeHour(timePickerView.getCurrentHour());
		//		schedule.setTimeMinute(timePickerView.getCurrentMinute());

		schedule.setDow(Calendar.MONDAY, dowMonView.isChecked());
		schedule.setDow(Calendar.TUESDAY, dowTueView.isChecked());
		schedule.setDow(Calendar.WEDNESDAY, dowWedView.isChecked());
		schedule.setDow(Calendar.THURSDAY, dowThuView.isChecked());
		schedule.setDow(Calendar.FRIDAY, dowFriView.isChecked());
		schedule.setDow(Calendar.SATURDAY, dowSatView.isChecked());
		schedule.setDow(Calendar.SUNDAY, dowSunView.isChecked());

	}

	//	protected void onSaveInstanceState(Bundle outState) {
	//		super.onSaveInstanceState(outState);
	//		saveState();
	//		outState.putParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE, todoUri);
	//	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClickHandler(View view) {
		//@SuppressWarnings("unchecked")
		//ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
		//Comment comment = null;
		switch (view.getId()) {
		case R.id.cancelButton:
			Log.v(TAG,"cancel button pressed - scheduleId = " + schedule.getId());
			finish();
			break;
		case R.id.deleteButton:
			Log.v(TAG,"delete button pressed - scheduleId = " + schedule.getId());
			datasource.deleteSchedule(schedule);
			profileSwitcher.setNextAlarm(); 
			finish();
			break;
		case R.id.saveButton:
			Log.v(TAG,"save button pressed - scheduleId = " + schedule.getId());
			getDataFromView();
			if (schedule.getProfile() == null) {
				Toast.makeText(ScheduleActivity.this, "Please pick a profile",
						Toast.LENGTH_LONG).show();
			} else {
				datasource.saveSchedule(schedule);
				profileSwitcher.setNextAlarm(); 
				setResult(RESULT_OK);
				finish();
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}       

}
