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


import java.util.ArrayList; 
import java.util.Calendar;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ScheduleDataSource {

	private static final String TAG = "ScheduleDataSource";
	// Database fields
	private SQLiteDatabase database;
	private ScheduleSQLiteHelper dbHelper;
	private String[] allColumns = { ScheduleSQLiteHelper.COLUMN_ID,
			ScheduleSQLiteHelper.COLUMN_PROFILE_NAME, 
			ScheduleSQLiteHelper.COLUMN_ENABLED, 
			ScheduleSQLiteHelper.COLUMN_TIME,
			ScheduleSQLiteHelper.COLUMN_DOW_MON,
			ScheduleSQLiteHelper.COLUMN_DOW_TUE,
			ScheduleSQLiteHelper.COLUMN_DOW_WED,
			ScheduleSQLiteHelper.COLUMN_DOW_THU,
			ScheduleSQLiteHelper.COLUMN_DOW_FRI,
			ScheduleSQLiteHelper.COLUMN_DOW_SAT,
			ScheduleSQLiteHelper.COLUMN_DOW_SUN };

	public ScheduleDataSource(Context context) {
		dbHelper = new ScheduleSQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Schedule saveSchedule(Schedule schedule) {
		long updatedScheduleId;
		ContentValues values = new ContentValues();
		values.put(ScheduleSQLiteHelper.COLUMN_PROFILE_NAME, schedule.getProfile());
		values.put(ScheduleSQLiteHelper.COLUMN_ENABLED, schedule.isEnabled());
		values.put(ScheduleSQLiteHelper.COLUMN_TIME, schedule.getTimeAsInt());
		values.put(ScheduleSQLiteHelper.COLUMN_DOW_MON, schedule.isDowActive(Calendar.MONDAY));
		values.put(ScheduleSQLiteHelper.COLUMN_DOW_TUE, schedule.isDowActive(Calendar.TUESDAY));
		values.put(ScheduleSQLiteHelper.COLUMN_DOW_WED, schedule.isDowActive(Calendar.WEDNESDAY));
		values.put(ScheduleSQLiteHelper.COLUMN_DOW_THU, schedule.isDowActive(Calendar.THURSDAY));
		values.put(ScheduleSQLiteHelper.COLUMN_DOW_FRI, schedule.isDowActive(Calendar.FRIDAY));
		values.put(ScheduleSQLiteHelper.COLUMN_DOW_SAT, schedule.isDowActive(Calendar.SATURDAY));
		values.put(ScheduleSQLiteHelper.COLUMN_DOW_SUN, schedule.isDowActive(Calendar.SUNDAY));
		if (schedule.getId() >= 1) {
			@SuppressWarnings("unused")
			int rowCount = database.update(ScheduleSQLiteHelper.TABLE_SCHEDULE, 
					values, ScheduleSQLiteHelper.COLUMN_ID + " = " + schedule.getId(), null);
			updatedScheduleId = schedule.getId();
			
		} else {
			updatedScheduleId = database.insert(ScheduleSQLiteHelper.TABLE_SCHEDULE, null,
				values);
		}
		
		Cursor cursor = database.query(ScheduleSQLiteHelper.TABLE_SCHEDULE,
				allColumns, ScheduleSQLiteHelper.COLUMN_ID + " = " + updatedScheduleId, null,
				null, null, null);
		cursor.moveToFirst();
		Schedule newSchedule = cursorToSchedule(cursor);
		cursor.close();
		return newSchedule;
	}

	public void deleteSchedule(Schedule Schedule) {
		long id = Schedule.getId();
		System.out.println("Schedule deleted with id: " + id);
		database.delete(ScheduleSQLiteHelper.TABLE_SCHEDULE, ScheduleSQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public Schedule getSchedule(long scheduleId) {

		Cursor cursor = database.query(ScheduleSQLiteHelper.TABLE_SCHEDULE,
				allColumns, ScheduleSQLiteHelper.COLUMN_ID + " = " + scheduleId, null, null, null, null);

	    if (cursor.getCount() != 1) {
	        cursor.close();
			Log.v(TAG,"Couldn't find scheduleId " + scheduleId);	        
	        return new Schedule();
	    }

	    cursor.moveToFirst();
		Schedule newSchedule = cursorToSchedule(cursor);
	    cursor.close();
		Log.v(TAG,"Found scheduleId " + scheduleId + " - Schedule:" + newSchedule.toString());	        
	    return newSchedule;
	}

	public List<Schedule> getAllSchedules() {
		List<Schedule> Schedules = new ArrayList<Schedule>();

		Cursor cursor = database.query(ScheduleSQLiteHelper.TABLE_SCHEDULE,
				allColumns, null, null, null, null, ScheduleSQLiteHelper.COLUMN_TIME);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Schedule Schedule = cursorToSchedule(cursor);
			Schedules.add(Schedule);
			cursor.moveToNext();
		}
		// Make sure to close the cursor
		cursor.close();
		return Schedules;
	}

	private Schedule cursorToSchedule(Cursor cursor) {
		Schedule schedule = new Schedule();
		schedule.setId(cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_ID)));
		schedule.setProfile(cursor.getString(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_PROFILE_NAME)));
		schedule.setTimeFromInt(cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_TIME)));
		schedule.setEnableFromInt(cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_ENABLED)));

 		schedule.setDowFromInt(Calendar.MONDAY, cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_DOW_MON)));
 		schedule.setDowFromInt(Calendar.TUESDAY, cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_DOW_TUE)));
 		schedule.setDowFromInt(Calendar.WEDNESDAY, cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_DOW_WED)));
 		schedule.setDowFromInt(Calendar.THURSDAY, cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_DOW_THU)));
 		schedule.setDowFromInt(Calendar.FRIDAY, cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_DOW_FRI)));
 		schedule.setDowFromInt(Calendar.SATURDAY, cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_DOW_SAT)));
 		schedule.setDowFromInt(Calendar.SUNDAY, cursor.getInt(cursor
				.getColumnIndexOrThrow(ScheduleSQLiteHelper.COLUMN_DOW_SUN)));
		return schedule;
	}
}
