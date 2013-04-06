package com.example.digitalmeasuringtape;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable, SensorEventListener{

	private String pi_string;
	private TextView tv;
	private ProgressDialog pd;
	private boolean activeThread = true;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mOrientation;
	private PhysicsManager physics;
	public SharedPreferences sPrefs;
	public TailLinkedList measurements;
	public float[] lastOrientation;
	public float firstOZ = -1; //every time "collect" is called, reset this to -1
	public CountDownLatch gate; //things call gate.await(), and get blocked.
								//things become unblocked when gate.countDown()
								//is called enough times, which will be 1
	
	protected void onExit()
	{
		if(mSensorManager != null)
			mSensorManager.unregisterListener(this);
		onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) this.findViewById(R.id.text1);
		tv.setText("--");
		
		sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		physics = new PhysicsManager(this);
		
		//setting up sensor managers
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		
		//hookup button
		Button button = (Button)findViewById(R.id.button1);
		button.setOnTouchListener(myListener);
		
		
		//check if Calibrated is true
		boolean CALIBRATED = sPrefs.getBoolean("calibrated_pref_check", false);

		if (!CALIBRATED){
			//popup and say we need to calibrate
			final Intent i = new Intent(this,Calibrate.class);		
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton("Ok. Calibrate me!", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int id){
					//continue
					dialog.dismiss();
					startActivity(i);
				}
			});
			builder.setMessage("We can't measure without Calibrating! Place the phone somewhere level and give us a few seconds to calibrate.").setTitle("WAIT!");
			AlertDialog dialog = builder.create();
			dialog.show();			
		}
		
	}
	
	@Override
	protected void onRestart(){
		super.onRestart();
		
		tv = (TextView) this.findViewById(R.id.text1);
		tv.setText("--");		
	}
	
	//temporary to make sure this app isn't the one draining my battery...
	@Override
	protected void onStop(){
		super.onStop();
		onDestroy();
	}
	
	private OnTouchListener myListener = new OnTouchListener(){
	    public boolean onTouch(View v, MotionEvent event) {
	        if(event.getAction() == MotionEvent.ACTION_DOWN) {
	            //start recording
	        	System.out.println("DOWN");
	        	start_distance_process();
	        	
	        } else if (event.getAction() == MotionEvent.ACTION_UP) {
	        	System.out.println("UP");
	        	//kill thread on release of button
				activeThread = false;
				if(gate!=null)
	            	gate.countDown(); 	
	        }
	        return true;
	    }
	};
	
	//connected to button's onClick
	public void start_distance_process(){
		
		//check if Calibrated is true
//		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//		boolean CALIBRATED = sharedPref.getBoolean("CALIBRATED", false);
//		System.out.println(CALIBRATED);
//		if (!CALIBRATED){
//			//if false, calibrate
//			Thread calibration_thread = new Thread(this);
//			calibration_thread.start();
//			//Calibrate_popup();
//			Intent i = new Intent(this,Calibrate.class);
//			startActivity(i);
//		}
				
		//start alert dialog
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setPositiveButton("FINIZH", new DialogInterface.OnClickListener(){
//			public void onClick(DialogInterface dialog, int id){
//				//kill thread on click
//				activeThread = false;
//				if(gate!=null)
//	            	gate.countDown(); 	
//			}
//		});
//		builder.setMessage("WERKING").setTitle("TWERKING");
//		AlertDialog dialog = builder.create();
//		
//		//temporary: move dialog down
//		WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
//		wmlp.gravity = Gravity.TOP | Gravity.LEFT;
//		wmlp.y = 400;
//		
//		dialog.show();
		
		//start thread
		System.out.println("Started distance process.");
		Thread thread = new Thread(this);
		thread.start();
	}
	
/************menu stuff**************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
//		Intent intent = new Intent(this, Settings.class);
//		startActivity(intent);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
//		case R.id.about_menuitem:
//			startActivity(new Intent(this, About.class));
		case R.id.settings_menuitem:
			startActivity(new Intent(this, Settings.class));
		}
		return true;
	}
	
/***********end menu stuff***********/	
	
	//put the code to be run during execution here.
	//this can be thought of as the main method of our thread.
	public void run()
	{		
		MeasureAndCalculateDistance();
	}
	
	public void MeasureAndCalculateDistance(){
		
		System.out.println("Calling MeasureAndCalculateDistance()");
		//make a fresh list, set gate as closed, register listener
		measurements = new TailLinkedList();
		lastOrientation = new float[3]; lastOrientation[0] = -1f; lastOrientation[1] = -1f; lastOrientation[2] = -1f;
		gate = new CountDownLatch(1);
		boolean worked = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);	
		boolean worked2 = mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_FASTEST);	
		
		
		System.out.println("Return from registerlistener: " + worked  + " and " + worked2);
		List<Sensor> l = mSensorManager.getSensorList(Sensor.TYPE_ALL);
		for(Sensor s : l)
			System.out.println(s.getName());
		
		try {
			gate.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//stop measuring
		mSensorManager.unregisterListener(this, mAccelerometer);
		mSensorManager.unregisterListener(this, mOrientation);
		
		pi_string = "calculating";
		handler.sendEmptyMessage(0);
		
		ArrayList<Float> xData = measurements.getxData();
		ArrayList<Float> yData = measurements.getyData();
		ArrayList<Float> zData = measurements.getzData();
		ArrayList<Float> oxData = measurements.getoxData();
		ArrayList<Float> oyData = measurements.getoyData();
		ArrayList<Float> ozData = measurements.getozData();
		ArrayList<Float> tData = measurements.gettData();
		
		physics.RemoveGravity(xData, yData, zData, oxData, oyData, ozData);
		
		double d = physics.Distance(xData, 
									yData,
									zData,
									tData);
		
		//d.toString(), then truncate to two decimal places
		String truncate;
		if(d == -1.0) truncate = "-1.0"; 
		if(d == 0) truncate = "0.0";
		else
		{
			String d_str = Double.valueOf(d).toString(); 
			truncate = d_str.substring(0, d_str.indexOf('.') + 3);
		}
		pi_string = truncate;
		
		//get shared setting for measurement units
		SharedPreferences sPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		String y = sPrefs.getString("meas_units", "0");
		int UNITS = Integer.valueOf(y);
		System.out.println("UNITS INT: " + UNITS);
		if (UNITS == 0)
		{
			//convert to feet
			System.out.println("pi_string: " + pi_string);
			double x = Double.parseDouble(pi_string) * 3.28084;
			
			//round value
			double range = 0.04;
			int factor = (int) Math.round(x/range);
			double result = factor * range;
			
			System.out.println("double pi_string: " + result);
			pi_string = Double.toString(result) + " ft";
		}
		else
		{
			pi_string = pi_string + " m";
		}
		
		handler.sendEmptyMessage(0);
		//pd.dismiss();
		System.out.println(truncate);
		System.out.println("returning from run()");
		}
	
	
	// manages user touching the screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        System.out.println("onTouchEvent fired");
    	
        if (activeThread && event.getAction() == MotionEvent.ACTION_DOWN) {
            // we set the activeThread boolean to false,
            // forcing the loop from the Thread to end
            activeThread = false;
            if(gate!=null)
            	gate.countDown(); 	//causes the thread's "run" method to contine.
            						//"opens the gate"
        }
        
        return super.onTouchEvent(event);
    }
    
	// manages user touching the screen
    public boolean stopMeasuring(MotionEvent event) {
        
        if (activeThread && event.getAction() == MotionEvent.ACTION_DOWN) {
            // we set the activeThread boolean to false,
            // forcing the loop from the Thread to end
            activeThread = false;
            gate.countDown(); //causes the thread's "run" method to contine.
            					//"opens the gate"
        }
        
        return super.onTouchEvent(event);
    }
	
	//Receive thread messages, interpret them and act as needed
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message mg){
			//pd.dismiss();
			tv.setText(pi_string);
		}
	};
	
	public void onSensorChanged(SensorEvent event) {
		//if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
			//return;
		
		switch(event.sensor.getType())
		{
		case Sensor.TYPE_ACCELEROMETER :
			if(lastOrientation[0] == -1f && lastOrientation[1] == -1f && lastOrientation[2] == -1f) {
				break;
			}
			float x = event.values[0]; 
			float y = event.values[1];
			float z = event.values[2];
			long t = event.timestamp; 
//			pi_string = "x = " + x + "\ny = " + y + "\nz = " + z;
			pi_string = "collecting";
			handler.sendEmptyMessage(0);
			measurements.add(x, y, z, lastOrientation[1], lastOrientation[2], lastOrientation[0], t); //record values.
			break;
		case Sensor.TYPE_ORIENTATION :
			if(firstOZ == -1){
				firstOZ = event.values[0];
			}
			System.out.println("Orientation Sensor Changed");
			lastOrientation[0] = event.values[0]; //oz
			lastOrientation[1] = event.values[1]; //ox
			lastOrientation[2] = event.values[2]; //oy
			long time = event.timestamp;
			//pi_string = "Azimuth¡ = " + lastOrientation[0] + "\nPitch¡ = " + lastOrientation[1] + "\nYaw¡ = " + lastOrientation[2];
			//System.out.println(pi_string);
			//handler.sendEmptyMessage(0);
			break;
			
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		//System.out.println("onAccuracyChanged fired");
		
	}

}
