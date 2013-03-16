package com.example.digitalmeasuringtape;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //deprecated, but we're developing for older stuff anyways... can be updated in the future
        addPreferencesFromResource(R.layout.settings);
    }
}
