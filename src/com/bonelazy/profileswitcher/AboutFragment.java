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
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;


public class AboutFragment extends DialogFragment implements OnClickListener {



	static AboutFragment newInstance(Context myContext) {
		return new AboutFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().setTitle("About ProfileSwitcher");
		View v = inflater.inflate(R.layout.about, container, false);		 
		WebView webView = (WebView) v.findViewById(R.id.webView);
//		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("file:///android_asset/about.html");
		return v;

	}
	
	public void onClick(View v) {

		Calendar alarmTime = Calendar.getInstance();
		alarmTime.set(Calendar.SECOND, 0);
		alarmTime.set(Calendar.MILLISECOND, 0);		
		//ProfileSwitcher.myLog("about - onClickHandler") ;

	}
}
