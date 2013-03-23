package com.example.digitalmeasuringtape;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Gravity;
import android.view.WindowManager;

public class Settings extends PreferenceActivity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //deprecated, but we're developing for older stuff anyways... can be updated in the future
        addPreferencesFromResource(R.layout.settings);
    }
/*	//can't call method from xml file
	void calibrate_sensor(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("FINIZH", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				//TODO when user clicks	
			}
		});
		builder.setMessage("WERKING").setTitle("TWERKING");
		AlertDialog dialog = builder.create();
		WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
		
		wmlp.gravity = Gravity.TOP | Gravity.LEFT;
		wmlp.y = 400;
		
		dialog.show();
		return;
	}
	*/
}
