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


import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


public class ScheduleArrayAdapter extends ArrayAdapter<Schedule> {
	@SuppressWarnings("unused")
	private static final String TAG = "ScheduleArrayAdapter";
	private final Context context;
	private final List<Schedule> values;
		
	public ScheduleArrayAdapter(Context context, List<Schedule> values) {
		super(context, R.layout.rowlayout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		TextView profileNameView = (TextView) rowView.findViewById(R.id.profileName);
		TextView timeTextView = (TextView) rowView.findViewById(R.id.timeDisplay);
		CheckBox enabledView = (CheckBox) rowView.findViewById(R.id.enabled);
		TextView daysOfWeekView = (TextView) rowView.findViewById(R.id.daysOfWeekView);
				
		profileNameView.setText(values.get(position).getProfile());
		timeTextView.setText(values.get(position).getTimeAsString());
		enabledView.setChecked(values.get(position).isEnabled());
		daysOfWeekView.setText(values.get(position).getDaysOfWeek(context));

		return rowView;
	}
}

	
