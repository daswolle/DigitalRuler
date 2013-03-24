package com.example.digitalmeasuringtape;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements Runnable, SensorEventListener{

	private String pi_string;
	private TextView tv;
	private ProgressDialog pd;
	private boolean activeThread = true;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private PhysicsManager physics;
	public SharedPreferences settings;
	public TailLinkedList measurements;
	public TailLinkedList angles;
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
		
		settings = getSharedPreferences("prefs", 0);
		physics = new PhysicsManager(this);
		
		//setting up sensor managers
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//TODO tv.setText(mAccelerometer.getMinDelay()); //can't b/c min API level 9 needed
		
	}
	
	//temporary to make sure this app isn't the one draining my battery...
	@Override
	protected void onStop(){
		super.onStop();
		onDestroy();
	}
	
	//connected to button's onClick
	public void start_distance_process(View view){
		
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setPositiveButton("FINIZH", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				//kill thread on click
				activeThread = false;
				if(gate!=null)
	            	gate.countDown(); 	
			}
		});
		builder.setMessage("WERKING").setTitle("TWERKING");
		AlertDialog dialog = builder.create();
		
		//temporary: move dialog down
		WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
		wmlp.gravity = Gravity.TOP | Gravity.LEFT;
		wmlp.y = 400;
		
		dialog.show();
		
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
		//check if Calibrated is true
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		boolean CALIBRATED = sharedPref.getBoolean("CALIBRATED", false);
		if (!CALIBRATED){
		Calibrate();
		}
		else{
		MeasureAndCalculateDistance();
		}
	}
	
	AlertDialog calibrate_dialog;
	public void Calibrate_popup(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("00:05").setTitle("CALIBRATING");
			calibrate_dialog = builder.create();
			calibrate_dialog.show();
			
			//TODO call Calibrate()
			
			new CountDownTimer(6000,1000){
				@Override
				public void onTick(long millisUntilFinished){
					calibrate_dialog.setMessage("00:" + (millisUntilFinished/1000));
				}
				
				@Override
				public void onFinish(){
					calibrate_dialog.dismiss();
				}
			}.start();
		return;
	}
	
	public void Calibrate()
	{	
		
		//call calibrate activity
		Intent intent = new Intent(this,Calibrate.class);
		startActivity(intent);
		
		
		System.out.println("Calling Calibrate()");
		//make a fresh list, set gate as closed, register listener
		measurements = new TailLinkedList();
//		gate = new CountDownLatch(1);
		boolean worked = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);	
		System.out.println("Return from registerlistener: " + worked );
		
		try {
			Thread.sleep(1000);
			//gate.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mSensorManager.unregisterListener(this, mAccelerometer);
		//mSensorManager.unregisterListener(this, mOrientation);
		ArrayList<Float> xData = measurements.getxData();
		ArrayList<Float> yData = measurements.getyData();
		ArrayList<Float> zData = measurements.getzData();
		
		float xAvg = 0, yAvg = 0, zAvg = 0;
		
		for(int i = 0; i < xData.size(); i ++)
		{
			xAvg += xData.get(i);
			yAvg += yData.get(i);
			zAvg += zData.get(i);
		}
		
		xAvg /= xData.size();
		yAvg /= yData.size();
		zAvg /= zData.size();
		
		SharedPreferences.Editor editor = settings.edit();
		System.out.println("Gx= " + xAvg + "\tGy= " + yAvg + "\tGz= " + zAvg);
		editor.putFloat("Gravity_x", xAvg);
		editor.putFloat("Gravity_y", yAvg);
		editor.putFloat("Gravity_z", zAvg);
		editor.putBoolean("CALIBRATED", true);
		
		editor.commit();
		
	}
	
	public void MeasureAndCalculateDistance(){
		
		System.out.println("Calling MeasureAndCalculateDistance()");
		//make a fresh list, set gate as closed, register listener
		measurements = new TailLinkedList();
		gate = new CountDownLatch(1);
		boolean worked = mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);	
		System.out.println("Return from registerlistener: " + worked );
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
		//mSensorManager.unregisterListener(this, mOrientation);
		ArrayList<Float> xData = measurements.getxData();
		ArrayList<Float> yData = measurements.getyData();
		ArrayList<Float> zData = measurements.getzData();
		ArrayList<Float> tData = measurements.gettData();
		
		physics.RemoveGravity(mSensorManager, xData, yData, zData);
		
		double d = physics.Distance(xData, 
									yData,
									zData,
									tData);
		
		System.out.println(measurements.getxString());
		
		/*******try writing xData to file***************/
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();
		
		if (Environment.MEDIA_MOUNTED.equals(state)){
			//We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
			//We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else{
			//Something is wrong
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		
		if (mExternalStorageAvailable && mExternalStorageWriteable){
			File path = Environment.getExternalStorageDirectory();
			File dir = new File (path.getAbsolutePath() + "/accData");
			dir.mkdirs();
			String filename = "myfile.txt";
			File file = new File(dir, filename);
			
			try {
//				path.mkdirs();
				System.out.println("Saving data to file");
				measurements.getxString();
				FileOutputStream outputStream = new FileOutputStream(file);
				outputStream.write(measurements.getxString().getBytes());
				outputStream.close();
				
				MediaScannerConnection.scanFile(this, 
						new String[] { file.toString() }, null, 
						new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						Log.i("ExternalStorage", "Scanned" + path + ":");
						Log.i("externalStorage", "-> uri=" + uri);
					}
				});
				
			} catch (IOException e){
				Log.w("ExternalStorage", "Error writing " + file, e);
			}
		}
		else{
			//System.out.println(mExternalStorageAvailable + " " + mExternalStorageWriteable);
			System.out.println("COULDN'T WRITE FILE BECAUSE STORAGE NOT AVAILABLE");
		}

		/*******************************************/
		
		//d.toString(), then truncate to two decimal places
		String truncate;
		if(d == -1.0) truncate = "-1.0"; 
		else
		{
			String d_str = Double.valueOf(d).toString(); 
			truncate = d_str.substring(0, d_str.indexOf('.') + 3);
		}
		pi_string = truncate;
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
			System.out.println("Accel Sensor changed");
			float x = event.values[0]; 
			float y = event.values[1];
			float z = event.values[2];
			long t = event.timestamp; 
			pi_string = "x = " + x + "\ny = " + y + "\nz = " + z;
			System.out.println(pi_string);
			handler.sendEmptyMessage(0);
			measurements.add(x, y, z, t); //record values.
			break;
		case Sensor.TYPE_ORIENTATION :
			System.out.println("Orientation Sensor Changed");
			float aboutz = event.values[0];
			float aboutx = event.values[1];
			float abouty = event.values[2];
			long time = event.timestamp;
			pi_string = "Azimuth¡ = " + aboutz + "\nPitch¡ = " + aboutx + "\nYaw¡ = " + abouty;
			System.out.println(pi_string);
			handler.sendEmptyMessage(0);
			angles.add(aboutz, aboutx, abouty, time);
			break;
			
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		//System.out.println("onAccuracyChanged fired");
		
	}

}
