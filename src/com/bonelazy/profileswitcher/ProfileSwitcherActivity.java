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
import java.util.List;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;


public class ProfileSwitcherActivity extends Activity  {


	private static final String TAG = "ProfileSwitcherActivity";
	private ScheduleArrayAdapter adapter;
	private ScheduleDataSource datasource;
	private ProfileSwitcher profileSwitcher;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final TextView TextView = null;    

	private TextView currentProfileView = TextView;
	private TextView caculatedCurrentProfileView = TextView;
	private TextView nextScheduledChangeView = TextView;
	private TextView timedProfileView = TextView;


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG,"STARTING....");
		setContentView(R.layout.main);
		datasource = new ScheduleDataSource(this);
		datasource.open();
		profileSwitcher = new ProfileSwitcher(this);
		//profileSwitcher.startService();

		currentProfileView = (TextView) findViewById(R.id.currentProfile);
		caculatedCurrentProfileView = (TextView) findViewById(R.id.caculatedCurrentProfile);
		nextScheduledChangeView = (TextView) findViewById(R.id.nextScheduledChange);
		timedProfileView = (TextView) findViewById(R.id.timedProfile);

		showAllSchedules();		
	}


	/** Called when the activity is first created. */

	//	@Override
	//	public void onCreate(Bundle savedInstanceState) {
	//		super.onCreate(savedInstanceState);
	//		setContentView(R.layout.todo_list);
	//		this.getListView().setDividerHeight(2);
	//		fillData();
	//		registerForContextMenu(getListView());
	//	}
	//
	// Create the menu based on the XML defintion
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem menuItem = menu.findItem(R.id.debug_mode);
		if (profileSwitcher.isDebugModeOn() ) {
			menuItem.setTitle("Turn off debug mode");
		} else {
			menuItem.setTitle("Turn on debug mode");
		}
		menuItem = menu.findItem(R.id.test_alarm);
		menuItem.setVisible(profileSwitcher.isDebugModeOn());

		menuItem = menu.findItem(R.id.reset_on_headset_plug);
		if (profileSwitcher.resetOnHeadsetPlug() ) {
			menuItem.setTitle("Headset plug:Reset profile");
		} else {
			menuItem.setTitle("Headset plug:Do nothing");
		}
		menuItem = menu.findItem(R.id.reset_on_headset_unplug);
		if (profileSwitcher.resetOnHeadsetUnplug() ) {
			menuItem.setTitle("Headset unplug:Reset profile");
		} else {
			menuItem.setTitle("Headset unplug:Do nothing");
		}
		return super.onPrepareOptionsMenu(menu);
	}

	
	// Reaction to the menu selection
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			displayAbout();
			return true;
		case R.id.timed_profile:
			timedProfile();
			return true;
		case R.id.manage_profiles:
			startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
			return true;
		case R.id.New:
			Intent i = new Intent(getApplicationContext(), ScheduleActivity.class);
			startActivity(i);			
			return true;
		case R.id.test_alarm:
			Calendar testCal = Calendar.getInstance();
			testCal.add(Calendar.SECOND, 10);
			profileSwitcher.setAlarm(testCal, ProfileSwitcher.REGULAR_ALARM);
			return true;
		case R.id.debug_mode:
			profileSwitcher.toggleDebugMode();
			return true;
		case R.id.reset_on_headset_plug:
			profileSwitcher.toggleResetOnHeadsetPlug();
			return true;
		case R.id.reset_on_headset_unplug:
			profileSwitcher.toggleResetOnHeadsetUnplug();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
			.getMenuInfo();
			Log.v(TAG,"in onContextItemSelected "+ info.toString());
			//			Uri uri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/"
			//					+ info.id);
			//			getContentResolver().delete(uri, null, null);
			//			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	//	private void createSchedule() {
	//		Log.v(TAG,"in createSchedule ");
	//		Schedule schedule = new Schedule();
	//		datasource.createSchedule(schedule);
	//		
	////		Intent i = new Intent(this, TodoDetailActivity.class);
	////		startActivityForResult(i, ACTIVITY_CREATE);
	//	}

	//	// Opens the second activity if an entry is clicked
	//	@Override
	//	protected void onListItemClick(ListView l, View v, int position, long id) {
	//		//super.onListItemClick(l, v, position, id);
	//		Log.v(TAG,"in onListItemClick");
	////		Intent i = new Intent(this, TodoDetailActivity.class);
	////		Uri todoUri = Uri.parse(MyTodoContentProvider.CONTENT_URI + "/" + id);
	////		i.putExtra(MyTodoContentProvider.CONTENT_ITEM_TYPE, todoUri);
	////
	////		// Activity returns an result if called with startActivityForResult
	////		startActivityForResult(i, ACTIVITY_EDIT);
	//	}

	// Called with the result of the other activity
	// requestCode was the origin request code send to the activity
	// resultCode is the return code, 0 is everything is ok
	// intend can be used to get data
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		Log.v(TAG,"SPSP ???? in onActivityResult");
		super.onActivityResult(requestCode, resultCode, intent);
	}

	//	private void fillData() {
	//
	//		// Fields from the database (projection)
	//		// Must include the _id column for the adapter to work
	//		String[] from = new String[] { TodoTable.COLUMN_SUMMARY };
	//		// Fields on the UI to which we map
	//		int[] to = new int[] { R.id.label };
	//
	//		getLoaderManager().initLoader(0, null, this);
	//		adapter = new SimpleCursorAdapter(this, R.layout.todo_row, null, from,
	//				to, 0);
	//
	//		setListAdapter(adapter);
	//	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		Log.v(TAG,"SPSP ???? in onCreateContextMenu");
		//menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	//	// Creates a new loader after the initLoader () call
	//	@Override
	//	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
	//		String[] projection = { TodoTable.COLUMN_ID, TodoTable.COLUMN_SUMMARY };
	//		CursorLoader cursorLoader = new CursorLoader(this,
	//				MyTodoContentProvider.CONTENT_URI, projection, null, null, null);
	//		return cursorLoader; 
	//	}

	//	@Override
	//	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
	//		adapter.swapCursor(data);
	//	}

	//	@Override
	//	public void onLoaderReset(Loader<Cursor> loader) {
	//		// data is not available anymore, delete reference
	//		adapter.swapCursor(null);
	//	}


	@Override
	protected void onResume() {
		Log.v(TAG,"in onResume");
		datasource.open();
		profileSwitcher.signalPossibleChangesMade();
		showAllSchedules();
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.v(TAG,"in onPause");
		Log.v(TAG,"SPSP in onPause");
		datasource.close();
		super.onPause();
	}       

	void  showAllSchedules() {
		//		List<Schedule> allSchedules;
		//		allSchedules = datasource.getAllSchedules();        
		//		ScheduleArrayAdapter adapter = new ScheduleArrayAdapter(this, allSchedules);
		//		setListAdapter(adapter);

		currentProfileView.setText(profileSwitcher.getActiveProfileName());
		caculatedCurrentProfileView.setText(profileSwitcher.getProfileToChangeTo());
		nextScheduledChangeView.setText(ProfileSwitcher.calendarToString(profileSwitcher.getNextScheduledAlarm()));
		Calendar timedProfileExires = profileSwitcher.getTimedProfileExires();
		if (timedProfileExires != null) {
			timedProfileView.setText("expires on " + ProfileSwitcher.calendarToEHHmm(timedProfileExires));
		} else {
			timedProfileView.setText("<none>");			
		}

		List<Schedule> allSchedules;
		allSchedules = datasource.getAllSchedules();        

		ListView scheduleList = (ListView) findViewById(R.id.schedulelist);
		adapter = new ScheduleArrayAdapter(this, allSchedules);
		scheduleList.setAdapter(adapter);


		scheduleList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> adapter, View view,
					int position, long id) {
				Log.v(TAG,"in onItemClick");
				Schedule schedule = (Schedule) adapter.getItemAtPosition(position);
				//						 Object info = adapter.getItemAtPosition(position);
				Intent i = new Intent(getApplicationContext(), ScheduleActivity.class);
				i.putExtra(ScheduleActivity.EXTRAS_SCHEDULE_ID, schedule.getId());
				startActivity(i);

			}
		});

	}

	private void timedProfile() {
		DialogFragment newFragment = TimedProfileFragment.newInstance(this);
		newFragment.show(getFragmentManager(), "dialog");
	}

	private void displayAbout(){
		DialogFragment newFragment = AboutFragment.newInstance(this);
		newFragment.show(getFragmentManager(), "dialog");
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setMessage(R.string.about)
//		       .setCancelable(true)
//		       ;
//		AlertDialog alert = builder.create();		
//		alert.show();
	}

}
