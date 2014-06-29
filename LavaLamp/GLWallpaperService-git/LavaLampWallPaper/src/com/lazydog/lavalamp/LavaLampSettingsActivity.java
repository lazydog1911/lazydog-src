/*
 * Copyright (C) 2010 Daniel Sundberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package com.glwallpaperservice.testing.wallpapers.nehe.lesson08;
package com.lazydog.lavalamp;


//import net.markguerra.android.glwallpapertest.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Settings for the wallpaper
 * 
 * TODO These preferences seem to have no effect
 * 
 * @author Daniel Sundberg
 * 
 */

//
/*
import android.preference.Preference;
import android.content.SharedPreferences;
 import android.content.SharedPreferences.OnSharedPreferddenceChangeListener;
*/

import android.preference.EditTextPreference;

import android.preference.Preference.OnPreferenceChangeListener ;
import android.preference.Preference;
import android.preference.PreferenceManager;

//
import android.util.Log;

//
import  android.content.SharedPreferences;

//for about button:
import  android.widget.Button;
import android.widget.ListView;
import android.view.View;
import android.content.Intent;
import android.content.Context;




public class LavaLampSettingsActivity extends PreferenceActivity {


	public Context context;
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		context = getApplicationContext();

//old way:
/*
		getPreferenceManager().setSharedPreferencesName(LavaLampWallpaperService.PREFERENCES);
		addPreferencesFromResource(R.xml.lavalamppreferences);

*/

SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		addPreferencesFromResource(R.xml.lavalamppreferences);
		PreferenceManager.setDefaultValues(this, R.xml.lavalamppreferences, false);

ListView v = getListView();
Button button = new Button(this);
button.setText("About");

       //set up license button on click
       button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
Log.v(this.getClass().getName(),"button onClick!");
                Intent i = new Intent(context, LicenseDialogActivity.class);
                startActivity(i);
            }});

        v.addFooterView(button);
        


final SharedPreferences.Editor editor = preferences.edit();
		String keys[] = { 
 "prefColorTopLeft"  , "prefColorTopRight", "prefColorBottomRight", "prefColorBottomLeft","prefColorBlobs" };

		for ( String key : keys ) 

		{
			EditTextPreference editTextPreference = (EditTextPreference) findPreference(key);
			editTextPreference.setSummary(editTextPreference.getText());

			//establish callback for this editTextPreference:
			editTextPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
public boolean onPreferenceChange(Preference preference, Object newValue) {

Log.v(this.getClass().getName(),"pref changed");
//update the display:
((EditTextPreference)(preference)).setSummary( (String) (newValue) );
//commit the change to preferences:
editor.putString(preference.getKey(), (String) (newValue) );
editor.commit();
	                return true;
                       }       
                   });   
                  }
	}
/*

public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    Preference pref = findPreference(key);

 //   if (pref instanceof ListPreference) {
 //       ListPreference listPref = (ListPreference) pref;
 //       pref.setSummary(listPref.getEntry());
 //   }
  Log.v(this.getClass().getName(),"onSharedPreferenceChanged:");

}
*/
}
