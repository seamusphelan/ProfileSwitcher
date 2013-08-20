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


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScheduleSQLiteHelper extends SQLiteOpenHelper {
	
	private static final String TAG = "ScheduleSQLiteHelper";
	
	public static final String TABLE_SCHEDULE = "schedule";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PROFILE_NAME = "profile_name";
	public static final String COLUMN_EXPIRE_MINUTES = "expire_minutes";
	public static final String COLUMN_ENABLED = "enabled";
	public static final String COLUMN_TIME = "time";
	public static final String COLUMN_DOW_MON = "dow_mon";
	public static final String COLUMN_DOW_TUE = "dow_tue";
	public static final String COLUMN_DOW_WED = "dow_wed";
	public static final String COLUMN_DOW_THU = "dow_thu";
	public static final String COLUMN_DOW_FRI = "dow_fri";
	public static final String COLUMN_DOW_SAT = "dow_sat";
	public static final String COLUMN_DOW_SUN = "dow_sun";

	private static final String DATABASE_NAME = "schedule.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_SCHEDULE + "(" + COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_PROFILE_NAME + " text not null, "
			+ COLUMN_EXPIRE_MINUTES + " UNSIGNED INTEGER (0, 1439)," 
			+ COLUMN_ENABLED + " UNSIGNED INTEGER (0, 1), "
			+ COLUMN_TIME + " UNSIGNED INTEGER (0, 1439)," 
			+ COLUMN_DOW_MON + " UNSIGNED INTEGER (0, 1), "
			+ COLUMN_DOW_TUE + " UNSIGNED INTEGER (0, 1), "
			+ COLUMN_DOW_WED + " UNSIGNED INTEGER (0, 1), "
			+ COLUMN_DOW_THU + " UNSIGNED INTEGER (0, 1), "
			+ COLUMN_DOW_FRI + " UNSIGNED INTEGER (0, 1), "
			+ COLUMN_DOW_SAT + " UNSIGNED INTEGER (0, 1), "
			+ COLUMN_DOW_SUN + " UNSIGNED INTEGER (0, 1) ); ";

//    + ALARMS_COL_DAY_OF_WEEK + " UNSIGNED INTEGER (0, 127), "
//    + ALARMS_COL_TIME + " UNSIGNED INTEGER (0, 86399),"
//    + ALARMS_COL_ENABLED + " UNSIGNED INTEGER (0, 1))");

	
	public ScheduleSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Log.v(TAG,"DATABASE_CREATE  = " + DATABASE_CREATE );
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(ScheduleSQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULE);
		onCreate(db);
	}

}
