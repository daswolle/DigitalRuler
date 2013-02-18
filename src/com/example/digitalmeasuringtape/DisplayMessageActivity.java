package com.example.digitalmeasuringtape;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayMessageActivity extends Activity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		
		//Make sure we're running on Honeycomb or higher to use ActionBar APIs
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		
		//create the text view
		TextView textView= new TextView(this);
		textView.setTextSize(40);
		textView.setText(message);
		
		//set the text view as the activity layout
		setContentView(textView);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()){
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
